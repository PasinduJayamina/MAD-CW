package com.example.novelonline.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.adapters.GenreAdapter
import com.example.novelonline.databinding.FragmentExploreBinding
import com.example.novelonline.models.Genre

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var genreAdapter: GenreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadGenres()
    }

    private fun setupRecyclerView() {
        genreAdapter = GenreAdapter { genre ->
            // Navigate to the GenreDetailFragment, passing the genre name
            val action = ExploreFragmentDirections.actionExploreFragmentToGenreDetailFragment(genre.name)
            findNavController().navigate(action)
        }
        binding.rvGenres.adapter = genreAdapter
    }

    private fun loadGenres() {
        // TODO: Replace this with your actual genre data from Firestore or a local list.
        val genres = listOf(
            Genre("Fantasy", "[https://placehold.co/80x80/1a2e07/ffffff?text=F](https://placehold.co/80x80/1a2e07/ffffff?text=F)"),
            Genre("Romance", "[https://placehold.co/80x80/7d1538/ffffff?text=R](https://placehold.co/80x80/7d1538/ffffff?text=R)"),
            Genre("Sci-Fi", "[https://placehold.co/80x80/0c4a6e/ffffff?text=S](https://placehold.co/80x80/0c4a6e/ffffff?text=S)"),
            Genre("Mystery", "[https://placehold.co/80x80/4a044e/ffffff?text=M](https://placehold.co/80x80/4a044e/ffffff?text=M)"),
            Genre("Thriller", "[https://placehold.co/80x80/7f1d1d/ffffff?text=T](https://placehold.co/80x80/7f1d1d/ffffff?text=T)"),
            Genre("Historical Fiction", "[https://placehold.co/80x80/78350f/ffffff?text=H](https://placehold.co/80x80/78350f/ffffff?text=H)"),
            Genre("Young Adult", "[https://placehold.co/80x80/065f46/ffffff?text=Y](https://placehold.co/80x80/065f46/ffffff?text=Y)"),
            Genre("Comedy", "[https://placehold.co/80x80/f59e0b/000000?text=C](https://placehold.co/80x80/f59e0b/000000?text=C)")
        )
        genreAdapter.submitList(genres)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}