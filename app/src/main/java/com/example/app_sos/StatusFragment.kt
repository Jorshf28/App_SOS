package com.example.app_sos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_sos.databinding.FragmentStatusBinding

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var statusAdapter: StatusAdapter
    private lateinit var statusList: MutableList<StatusItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        statusList = mutableListOf()
        statusAdapter = StatusAdapter(statusList)
        binding.recyclerViewStatus.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStatus.adapter = statusAdapter

        // Populate the list with data (replace with your logic)
        loadStatusData()

        // Set button click listener for refreshing the status list
        binding.btnRefresh.setOnClickListener {
            refreshStatusList()
        }
    }

    // Sample function to load status data (replace with actual data fetching logic)
    private fun loadStatusData() {
        statusList.add(StatusItem("Report 1", "Description of report 1"))
        statusList.add(StatusItem("Report 2", "Description of report 2"))
        statusList.add(StatusItem("Report 3", "Description of report 3"))

        statusAdapter.notifyDataSetChanged()
    }

    // Function to refresh the status list
    private fun refreshStatusList() {
        // Logic to refresh the list (e.g., re-fetching data from server)
        statusList.clear()
        loadStatusData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}