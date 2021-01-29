package eu.icard.androidmobilepaymentsipgsdk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class PopUpWindowAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, R.layout.item_popup, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater    = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view            = inflater.inflate(R.layout.item_popup, null)

            viewHolder      = ViewHolder(view)
            view.tag        = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.label.text = items[position]

        return view
    }

    class ViewHolder(view: View){
        val label: TextView = view.findViewById(R.id.label_txt)
    }
}