package pt.ipt.dam.pocketknowledge

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class inside_flashcardActivity : AppCompatActivity() {

    private lateinit var firstLayout: LinearLayout
    private lateinit var secondLayout: LinearLayout
    private lateinit var gestureDetector: GestureDetector
    private lateinit var sensorManager: SensorManager
    private var shakeDetector: ShakeDetector? = null

    private lateinit var favoriteButton: ImageButton // Botão de favoritos
    private var isFavorited: Boolean = false // Estado inicial

    private lateinit var question_text: TextView // Elemento pergunta
    private lateinit var answer_text: TextView // Elemento resposta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inside_flashcard)

        // Inicializa os layouts
        firstLayout = findViewById(R.id.first_layout)
        secondLayout = findViewById(R.id.second_layout)

        // Inicializa o botão de favoritos
        favoriteButton = findViewById(R.id.favoriteButtonInside)

        favoriteButton.setOnClickListener {
            toggleFavorite()
        }

        // Configura o GestureDetector para detectar dois cliques
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(event: MotionEvent): Boolean {
                toggleLayout() // Alterna os layouts ao detectar dois cliques
                return true
            }
        })

        // Configura o ShakeDetector para detectar agitação
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector = ShakeDetector { toggleLayout() }

        // Registrar o listener de shake
        accelerometer?.let {
            sensorManager.registerListener(shakeDetector, it, SensorManager.SENSOR_DELAY_UI)
        }

        // Configura o listener de toque para ambos os layouts
        val touchListener = View.OnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
        firstLayout.setOnTouchListener(touchListener)
        secondLayout.setOnTouchListener(touchListener)

        // Elemento pergunta
        question_text = findViewById(R.id.question_text)
        // Elemento answer
        answer_text = findViewById(R.id.answer_text)

        // Elemento Botao de voltar
        val backButton : Button = findViewById(R.id.BackButtonAllFlashcards)
        backButton.setOnClickListener(){
            val intent = Intent(this, all_flashcards_screenActivity::class.java)
            startActivity(intent)
        }

        // Obter o ID do Flashcard passado pela Intent
        val flashcardId = intent.getIntExtra("FLASHCARD_ID", -1)

        if (flashcardId != -1) {
            fetchFlashcardById(flashcardId)
        } else {
            Toast.makeText(this, "Erro ao carregar flashcard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFavorite() {
        // Obtém o drawable do botão e verifica se é uma animação
        val drawable = favoriteButton.drawable
        if (drawable is AnimatedVectorDrawable) {
            drawable.start() // Inicia a animação em dispositivos Android 5.0+ (Lollipop)
        } else if (drawable is AnimatedVectorDrawableCompat) {
            drawable.start() // Compatível com versões mais antigas do Android
        }

        // Alterna o estado de favorito e atualiza o ícone
        isFavorited = !isFavorited
        favoriteButton.setImageResource(
            if (isFavorited) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24
        )
    }

    private fun toggleLayout() {
        // Alterna entre os dois layouts
        if (firstLayout.visibility == View.VISIBLE) {
            firstLayout.visibility = View.GONE
            secondLayout.visibility = View.VISIBLE
        } else {
            firstLayout.visibility = View.VISIBLE
            secondLayout.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        shakeDetector?.let {
            sensorManager.unregisterListener(it) // Desregistra o listener do acelerómetro
        }
    }

    // Classe ShakeDetector para detectar agitação
    class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

        private val shakeThreshold = 20.0f // Limite para considerar agitação
        private val shakeTimeLapse = 1000 // Intervalo mínimo entre agitações (ms)
        private var lastShakeTime: Long = 0

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    // Calcula a força G
                    val gForce = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                    if (gForce > shakeThreshold) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastShakeTime > shakeTimeLapse) {
                            lastShakeTime = currentTime
                            onShake() // Chama a função de agitação
                        }
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Não necessário para esta implementação
        }
    }

    // Função para buscar um flashcard específico da API
    private fun fetchFlashcardById(flashcardId: Int) {
        // Fazer a chamada à API
        val call = RetrofitInitializer().apiService().getFlashcardById(flashcardId)

        // Callbacks
        call.enqueue(object : Callback<flashcards> {
            override fun onResponse(call: Call<flashcards>, response: Response<flashcards>) {
                // Verifica se a resposta é bem sucedida
                if (response.isSuccessful) {
                    // Obter o flashcard
                    val flashcard = response.body()
                    // Verifica se o flashcard não é nulo
                    if (flashcard != null) {
                        question_text.text = flashcard.question
                        answer_text.text = flashcard.answer
                    }
                } else {
                    Toast.makeText(applicationContext, "Flashcard não encontrado.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<flashcards>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro na conexão.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
