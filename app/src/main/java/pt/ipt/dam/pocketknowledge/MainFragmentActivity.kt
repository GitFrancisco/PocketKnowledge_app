package pt.ipt.dam.pocketknowledge
import CategoriesFragment
import FavoritesFragment
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.ipt.dam.pocketknowledge.R
import pt.ipt.dam.pocketknowledge.model.userData
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragmentActivity : AppCompatActivity() {

    private lateinit var textUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_layout)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Define o fragmento inicial ao abrir a tela
        if (savedInstanceState == null) {
            replaceFragment(CategoriesFragment())
        }

        // Configura a navegação para alternar entre os fragmentos
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_categories -> replaceFragment(CategoriesFragment())
                R.id.nav_favorites -> replaceFragment(FavoritesFragment())
            }
            true
        }

        // Elemento TextView para mostrar o nome do utilizador
        textUser = findViewById(R.id.usernameTextView)
        fetchUserData()

        // Elemento Botao de Logout
        val logoutButton: Button = findViewById(R.id.LogoutButton)
        logoutButton.setOnClickListener {
            // Limpar o token de autenticação
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // Redirecionar para a tela de login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Método para buscar os dados do utilizador autenticado
    private fun fetchUserData() {
        // Recuperar o token guardado no SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        // Criar uma instância do RetrofitInitializer e acessar o serviço da API
        val apiService = RetrofitInitializer().apiService()

        // Fazer uma chamada à API para obter os dados do utilizador autenticado
        apiService.getUserData("Bearer $token").enqueue(object : Callback<userData> {
            override fun onResponse(call: Call<userData>, response: Response<userData>) {
                if (response.isSuccessful) {
                    // Obter os dados do utilizador
                    val userData = response.body()
                    // Verificar se os dados do utilizador não são nulos
                    if (userData != null) {
                        val user = userData
                        textUser.text = "Bem-vindo, ${user.username}!"
                    } else {
                        Toast.makeText(applicationContext, "Volte a fazer login.", Toast.LENGTH_SHORT).show()
                        //Redirecionar para a tela de login
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(applicationContext, "Erro ao buscar dados do utilizador.", Toast.LENGTH_SHORT).show()
                    //Redirecionar para a tela de login
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            // Exibe uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<userData>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro ao conectar ao servidor. Reinicie a aplicação.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}