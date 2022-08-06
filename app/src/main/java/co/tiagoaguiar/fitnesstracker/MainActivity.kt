package co.tiagoaguiar.fitnesstracker

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {


    private lateinit var rvMain: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lista de estrutura de dados
        val mainItems = mutableListOf<MainItem>()
        mainItems.add(
            MainItem(
                id = 1,
                drawable = R.drawable.ic_baseline_wb_sunny_24,
                textStringId = R.string.label_imc,
                color = Color.GREEN
            )
        )
        mainItems.add(
            MainItem(
                id = 2,
                drawable = R.drawable.ic_baseline_visibility_24,
                textStringId = R.string.label_tmb,
                color = Color.GRAY
            )
        )

        /*
        1) o layout XML
        2) a onde a recyclerview vai aparecer (tela principal, tela cheia)
        3) logica - conectar o xml da celula DENTRO do recyclerView + a sua qtd de elementos dinamicos
         */

        /*
        val adapter = MainAdpter(mainItems, object : OnItemClickListener {
            // METODO 2: IMPL VIA OBJETO ANONIMO
            override fun onClick(id: Int) {
                when(id) {
                    1 -> {
                        val intent = Intent(this@MainActivity, ImcActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {
                        // Abrir uma outra activity
                    }
                }
                Log.i("Teste", "clicou $id!")
            }

        }) // Objeto
         */

        // Lista de dados para exibir na tela
        val adapter = MainAdpter(mainItems) { id ->
            // METODO 3: IMPL VIA FUNCTIONS
            when (id) {
                1 -> {
                    val intent = Intent(this@MainActivity, ImcActivity::class.java)
                    startActivity(intent)

                }
                2 -> {
                    // Abrir uma outra activity (TMB)
                    val intent = Intent(this@MainActivity, TmbActivity::class.java)
                    startActivity(intent)

                }
            }
            Log.i("Teste", "clicou $id!")
        }


        rvMain = findViewById(R.id.rv_main)
        rvMain.adapter = adapter // Anexou o adptador
        rvMain.layoutManager = GridLayoutManager(this,2) // O comportamento que vai exibir os dados

        /*
        classe para administrar a recyclerView e suas celulas (os seus layouts de itens)
        Adapter -> Adptador
         */

    }

    /*
    METODO 1 : USANDO IMPL INTERFACE VIA ACTIVITY
    override fun onClick(id: Int) {
        when(id) {
            1 -> {
                val intent = Intent(this, ImcActivity::class.java)
                startActivity(intent)
            }
            2 -> {
                // Abrir uma outra activity
            }
        }
        Log.i("Teste", "clicou $id!")
    }
     */


    // Métodos abstratos
    // Adptador da Tela Principal
    private inner class MainAdpter(
        private val mainItems: List<MainItem>,
   //     private val onItemClickListener: OnItemClickListener
    private val onItemClickListener: (Int) -> Unit,
        ) : RecyclerView.Adapter<MainAdpter.MainViewHolder>(){

        // 1 - Informar - Qual é o layout XML da celula especifica (item)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = layoutInflater.inflate(R.layout.main_item, parent, false)
            return MainViewHolder(view)
        }

        // 2 - disparado toda vez houver uma rolagem na tela e for necessario trocar o conteudo
        // da celula
        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            val itemCurrent = mainItems[position]
            holder.bind(itemCurrent)

        }

        // 3 - Informar quantas celulas essa listagem terá
        override fun getItemCount(): Int {
            return  mainItems.size // x elementos

        }

        // É a classe da celula em si!!!
        // É classe que buscar referencias quando tiver varios button
        private inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: MainItem) {
                val img: ImageView = itemView.findViewById(R.id.item_img_icon)
                val name: TextView = itemView.findViewById(R.id.item_txt_name)
                val container: LinearLayout = itemView.findViewById(R.id.item_container_imc)

                img.setImageResource(item.drawable)
                name.setText(item.textStringId)
                container.setBackgroundColor(item.color)

                container.setOnClickListener {

                    //onItemClickListener.onClick(item.id) -> Aqui é uma interface

                    // aqui ele é uma ref. função
                    onItemClickListener.invoke(item.id)
                }
            }
        }
    }


    // 3 maneiras de escutar eventos de click usando celula (viewholder) actvities
    // 1. [x] implementação interface
    // 2. [x] objetos anonimos
    // 3. [x] funcional -> Recomendavel
}