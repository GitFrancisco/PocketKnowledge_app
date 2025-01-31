package pt.ipt.dam.pocketknowledge
import CategoriesFragment
import FavoritesFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.ipt.dam.pocketknowledge.R

class MainFragmentActivity : AppCompatActivity() {

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
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}