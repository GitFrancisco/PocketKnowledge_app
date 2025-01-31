import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.pocketknowledge.R


class CategoriesFragment : Fragment(R.layout.categories_screen) {
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
    }
}
