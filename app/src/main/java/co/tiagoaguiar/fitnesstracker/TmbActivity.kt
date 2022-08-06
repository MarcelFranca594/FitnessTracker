package co.tiagoaguiar.fitnesstracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import co.tiagoaguiar.fitnesstracker.model.Calc
import kotlin.math.log

class TmbActivity : AppCompatActivity() {

    private lateinit var lifestyle: AutoCompleteTextView
    private lateinit var editWeight: EditText // Peso
    private lateinit var editHeight: EditText // Altura
    private lateinit var editAge: EditText // Idade


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmb)

        editWeight = findViewById(R.id.edit_tmb_weight)
        editHeight = findViewById(R.id.edit_tmb_height)
        editAge = findViewById(R.id.edit_tmb_age)

        lifestyle = findViewById(R.id.auto_lifestyle)
        val items = resources.getStringArray(R.array.tmb_lifestyle)
        lifestyle.setText(items.first())
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lifestyle.setAdapter(adapter)

        val btnSend: Button = findViewById(R.id.btn_tmb_send)
        btnSend.setOnClickListener {
            if (!validate()) { // Se não for valido ...
                // Enviando um msg para o usuário que algo deu errado
                // Acessando Recursos - Classe R
                Toast.makeText(this, R.string.fields_messages, Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Matar a execução desse bloco do código
            }

            val weight = editWeight.text.toString().toInt() // Convertendo para Inteiro
            val height = editHeight.text.toString().toInt()
            val age = editAge.text.toString().toInt()

            val result = calculateTmb(weight, height, age)
            val response = tmbRequest(result)

            AlertDialog.Builder(this)
                // Opção -> Utilizando lambda
                .setMessage(getString(R.string.tmb_responce, response)) //
                .setPositiveButton(android.R.string.ok) { dialog, which -> // Texto Botao ok
                }
                .setNegativeButton(R.string.save) { dialog, which -> // Texto Botao Salvar
                    Thread {
                        val app = application as App
                        val dao = app.db.calcDao()
                        dao.insert(Calc(type = "tmb", res = response))

                        runOnUiThread {
                            // Abrir uma segunda tela
                            openListActivity()
                        }

                    }.start()
                }
                .create()
                .show()

            // Gerenciando Teclado -> Esconder o teclado
            val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_search) {
            finish()
            openListActivity()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun openListActivity(){
        val intent = Intent(this, ListCalcActivity::class.java)
        intent.putExtra("type","tmb")
        startActivity(intent)
    }

    private fun tmbRequest(tmb: Double): Double{
        val items = resources.getStringArray(R.array.tmb_lifestyle)
        return when {
            lifestyle.text.toString() == items[0] -> tmb * 1.2
            lifestyle.text.toString() == items[1] -> tmb * 1.375
            lifestyle.text.toString() == items[2] -> tmb * 1.55
            lifestyle.text.toString() == items[3] -> tmb * 1.725
            lifestyle.text.toString() == items[4] -> tmb * 1.9
            else -> 0.0
        }
    }

    private fun calculateTmb(weight: Int, height: Int, age: Int): Double{
        // Cálculo do IMC -> peso / (altura * altura)
        return 66 + (13.8 * weight) + (5 * height) - (6.8 * age)
    }

    private fun validate(): Boolean {
            // não pode inserir valores nulos (vazio)
            // não pode inserir/ começar com 0
            // startsWith verifica se a string inicia com o valor de string especificado

            // Opção 3: Retorna direto o que for verdadeiro
            return (editWeight.text.toString().isNotEmpty()
                    && editHeight.text.toString().isNotEmpty()
                    && editAge.text.toString().isNotEmpty()
                    && !editWeight.text.toString().startsWith("0") // Vai ser verdadeiro quando não começar com zero
                    && !editHeight.text.toString().startsWith("0")
                    && !editAge.text.toString().startsWith("0"))
    }
}


