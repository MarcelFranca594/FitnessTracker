package co.tiagoaguiar.fitnesstracker

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import co.tiagoaguiar.fitnesstracker.model.Calc

class ImcActivity : AppCompatActivity() {


    private lateinit var editWeight: EditText // Peso
    private lateinit var editHeight: EditText // Altura

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imc)

        // Buscar os objetos e ter a referencia deles
        // findViewById - Localizando uma visualização pelo id
        editWeight = findViewById(R.id.edit_imc_weight)
        editHeight = findViewById(R.id.edit_imc_height)

        val btnSend: Button = findViewById(R.id.btn_imc_send)

        // Evento de clicl
        btnSend.setOnClickListener{
        // Erro -> Validação de Formulario
            // chamando a função validate()
            if (!validate()){ // Se não for valido ...
                // Enviando um msg para o usuário que algo deu errado
                // Acessando Recursos - Classe R
                Toast.makeText(this, R.string.fields_messages, Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Matar a execução desse bloco do código
            }
            // Caso for valido
            // Sucesso - Cálculo

            val weight = editWeight.text.toString().toInt() // Convertendo para Inteiro
            val height = editHeight.text.toString().toInt()


            // a função calculateImc com os parametros weigth e heigth. Onde o result vai receber essa função
            val result = calculateImc(weight, height)
            Log.d("Teste","resultado: $result") // Log.d -> debug

            val imcResponseId = imcResponse(result) // Chamando a função imcResponse e passando o result

            // Criando Dialogs / PopUps
            AlertDialog.Builder(this)
            // Opção -> Utilizando lambda
                .setTitle(getString(R.string.imc_responce, result)) // Titutlo
                .setMessage(imcResponseId) // Calculo -> imcResponseId
                .setPositiveButton(android.R.string.ok) { dialog, which -> // Texto Botao ok
                // aqui vai rodar depois do click
            }
                .setNegativeButton(R.string.save) { dialog, which -> // Texto Botao Salvar
                    Thread {
                        val app = application as App
                        val dao = app.db.calcDao()
                        dao.insert(Calc(type = "imc", res = result))

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
        intent.putExtra("type","imc")
        startActivity(intent)
    }

    // 3° - Função imcResponse
    // Conversão de n° para textos (está no arquivo de strings.xml)
    @StringRes // Inteiro de Recurso
    private fun imcResponse(imc: Double): Int{
        return when {
            imc < 15.0 -> R.string.imc_severely_low_weight
            imc < 16.0 -> R.string.imc_very_low_weight
            imc < 18.5 -> R.string.imc_low_weight
            imc < 25.0 -> R.string.normal
            imc < 30.0 -> R.string.imc_high_weight
            imc < 35.0 -> R.string.imc_so_high_weight
            imc < 40.0 -> R.string.imc_severely_high_weight
            else -> R.string.imc_extreme_weight
        }
    }

     // 2° - Função calculateImc
    private fun calculateImc(weight: Int, height: Int): Double{
        // Cálculo do IMC -> peso / (altura * altura)
        return weight / ((height / 100.0) * (height / 100.0))
    }

    // 1° - Função de validação
    private fun validate(): Boolean {
        // não pode inserir valores nulos (vazio)
        // não pode inserir/ começar com 0
        // startsWith verifica se a string inicia com o valor de string especificado

        // Opção 3: Retorna direto o que for verdadeiro
            return (editWeight.text.toString().isNotEmpty()
            && editHeight.text.toString().isNotEmpty()
            && !editWeight.text.toString().startsWith("0") // Vai ser verdadeiro quando não começar com zero
            && !editHeight.text.toString().startsWith("0"))
    }
}

// true && true = true
// true && false = false
// false && true = false
// false && false = false

// Opção 1: Usar if e else
/*
if (editWeight.text.toString().isNotEmpty()
    && editHeight.text.toString().isNotEmpty()
    && !editWeight.text.toString().startsWith("0") -> "!" negação onde não vai começar com 0
    && !editHeight.text.toString().startsWith("0")
){
    return true
} else {
    return false
}

 */

// Opção 2: usar somente return para simular o if/else
/*
if (editWeight.text.toString().isNotEmpty()
    && editHeight.text.toString().isNotEmpty()
    && !editWeight.text.toString().startsWith("0")
    && !editHeight.text.toString().startsWith("0"))
){
    return true
}
    return false
}
 */