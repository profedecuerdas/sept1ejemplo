
package com.example.sept1ejemplo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sept1ejemplo.database.AppDatabase
import com.example.sept1ejemplo.database.RegistroEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RegistrosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        recyclerView = findViewById(R.id.recyclerViewRegistros)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RegistrosAdapter(emptyList())
        recyclerView.adapter = adapter

        cargarRegistros()
    }

    private fun cargarRegistros() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(this@RegistrosActivity)
            val registros = database.registroDao().getAllRegistros()
            withContext(Dispatchers.Main) {
                adapter.actualizarRegistros(registros)
            }
        }
    }
}

class RegistrosAdapter(private var registros: List<RegistroEntity>) : RecyclerView.Adapter<RegistrosAdapter.RegistroViewHolder>() {
    class RegistroViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        // TODO: Definir los elementos de la vista para cada item
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RegistroViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        // TODO: Configurar los elementos de la vista con los datos del registro
    }

    override fun getItemCount() = registros.size

    fun actualizarRegistros(nuevosRegistros: List<RegistroEntity>) {
        registros = nuevosRegistros
        notifyDataSetChanged()
    }
}

