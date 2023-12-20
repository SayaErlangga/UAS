// WishlistFragment.kt

package com.example.uas.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.TripAdapter
import com.example.uas.Trips
import com.example.uas.TripsAdapter
import com.example.uas.TripsRoomDatabase
import com.example.uas.WtripDao
import com.example.uas.databinding.FragmentDashboardBinding
import com.example.uas.databinding.FragmentWishlistBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WishlistFragment : Fragment() {
    private lateinit var wtripDao: WtripDao
    private lateinit var executorService: ExecutorService
    private var _binding: FragmentWishlistBinding? = null
    private lateinit var tripsAdapter: TripsAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        executorService = Executors.newSingleThreadExecutor()
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        val db = TripsRoomDatabase.getInstance(requireContext())
        wtripDao = db!!.tripsDao()!!
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripsAdapter = TripsAdapter(
            onClickTrips = { /* provide implementation for onClickTrips */ },
            onDelete = {
                    trip -> delete(trip)
            }
        )
        with(binding) {
            rvWishlist.layoutManager = LinearLayoutManager(requireContext())
            rvWishlist.adapter = tripsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        getAllTrips()
    }

    private fun getAllTrips() {
        wtripDao.allClasses.observe(viewLifecycleOwner) { favorite ->
            tripsAdapter.submitList(favorite)
        }
    }

//    private fun delete(trips: Trips){
//        executorService.execute {
//            wtripDao.delete(trips)
//        }
//    }

    private fun delete(trips: Trips) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order?")
            .setPositiveButton("Delete") { dialog, which ->
                // User confirmed, proceed with deletion
                executorService.execute {
                    wtripDao.delete(trips)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}