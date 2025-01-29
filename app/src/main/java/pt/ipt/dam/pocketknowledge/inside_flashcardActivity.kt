package pt.ipt.dam.pocketknowledge

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class inside_flashcardActivity : AppCompatActivity() {

    private lateinit var firstLayout: LinearLayout
    private lateinit var secondLayout: LinearLayout
    private lateinit var gestureDetector: GestureDetector
    private lateinit var sensorManager: SensorManager
    private var shakeDetector: ShakeDetector? = null
    private lateinit var favoriteButton: ImageButton
    private var isFavorited = false // Estado do favorito

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inside_flashcard)

        // Inicializa os layouts
        firstLayout = findViewById(R.id.first_layout)
        secondLayout = findViewById(R.id.second_layout)
        favoriteButton = findViewById(R.id.favoriteButtonInside)

        // Configura o GestureDetector para detectar dois cliques
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(event: MotionEvent): Boolean {
                toggleLayout()
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

        // Configura o listener de toque para alternar layouts
        val touchListener = View.OnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
        firstLayout.setOnTouchListener(touchListener)
        secondLayout.setOnTouchListener(touchListener)

        // Configura o clique no botão de favoritos
        favoriteButton.setOnClickListener {
            toggleFavorite()
        }
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

    private fun toggleFavorite() {
        val avd = favoriteButton.drawable as AnimatedVectorDrawable
        avd.start() // Inicia a animação
        isFavorited = !isFavorited // Alterna o estado do favorito
    }

    override fun onDestroy() {
        super.onDestroy()
        shakeDetector?.let {
            sensorManager.unregisterListener(it) // Desregistra o listener do acelerômetro
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
}
