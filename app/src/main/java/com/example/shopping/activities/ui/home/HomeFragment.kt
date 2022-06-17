package com.example.shopping.activities.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.CartListActivity
import com.example.shopping.activities.ProductDetailsActivity
import com.example.shopping.activities.SettingsActivity
import com.example.shopping.activities.ui.dashboard.BaseFragment
import com.example.shopping.adapter.DashboardItemsListAdapter
import com.example.shopping.databinding.FragmentHomeBinding
import com.example.shopping.firestore.FirestoreClass
import com.example.shopping.models.Constants
import com.example.shopping.models.Product
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_settings -> {

                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart->{
                startActivity(Intent(activity,CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//            textView.text =  "This is Dashboard Fragment"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun successDashboardItemsList(dashboadItemsList:ArrayList<Product>){
        hideProgressDialog()
        if(dashboadItemsList.size>0){
            rv_dashboard_items.visibility=View.VISIBLE
            tv_no_dashboard_items_found.visibility=View.GONE

            rv_dashboard_items.layoutManager=GridLayoutManager(activity,2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter=DashboardItemsListAdapter(requireActivity(),dashboadItemsList)
            rv_dashboard_items.adapter=adapter

//            adapter.setOnClickListener(object :DashboardItemsListAdapter.OnClickListener{
//                override fun onClick(position: Int, product: Product) {
//                    val intent=Intent(context,ProductDetailsActivity::class.java)
//                    intent.putExtra(Constants.EXTRA_PRODUCT_ID,product.product_id)
//                    startActivity(intent)
//                }
//            })
        }else{
            rv_dashboard_items.visibility=View.GONE
            tv_no_dashboard_items_found.visibility=View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }
    private fun getDashboardItemsList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this)
    }
}