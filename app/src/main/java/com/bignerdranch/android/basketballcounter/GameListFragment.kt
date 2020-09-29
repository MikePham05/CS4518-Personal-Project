package com.bignerdranch.android.basketballcounter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "GameListFragment"



class GameListFragment : Fragment() {
    /**
    interface for hosting activities
     */
    interface CallBacks {
        fun onGameSelected(gameId: UUID)
    }

    private var callbacks: GameListFragment.CallBacks? = null
    private lateinit var gameRecyclerView: RecyclerView
    private var adapter: GameAdapter? = GameAdapter(emptyList())
    private val gameListViewModel : GameListViewModel by lazy {
        ViewModelProviders.of(this).get(GameListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as CallBacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_list, container, false)
        gameRecyclerView = view.findViewById(R.id.game_recycler_view) as RecyclerView
        gameRecyclerView.layoutManager = LinearLayoutManager(context)
        gameRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameListViewModel.gameListLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { games ->
                games?.let {
                    Log.i("idd", "GOT GAMES ${games.size}")
                    updateUI(games)
                }
            }
        )
        Log.i(TAG, "${gameListViewModel.gameListLiveData.value}")
    }

    private fun updateUI(games: List<Game>) {
        var adapter = GameAdapter(games)
        gameRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): GameListFragment {
            return GameListFragment();
        }
    }

    private inner class GameHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private lateinit var game: Game
        val gameTimeTextView: TextView = itemView.findViewById(R.id.game_time)
        val teamsNameTextView: TextView = itemView.findViewById(R.id.teams_name)
        val gameScoreTextView: TextView = itemView.findViewById(R.id.itemGameScoreTextView)
        val winningteamImageView: ImageView = itemView.findViewById(R.id.winningTeamImageView)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(game: Game) {
            this.game = game
            gameTimeTextView.text = "${game.id}    " + game.date.toString()
            teamsNameTextView.text = game.teamAName + "   :   " + game.teamBName
            gameScoreTextView.text = game.teamAScore.toString() + ":" + game.teamBScore.toString()
            if (game.teamAScore >= game.teamBScore) {
                winningteamImageView.setImageResource(R.drawable.team_a)
            } else {
                winningteamImageView.setImageResource(R.drawable.team_b)
            }
        }

        override fun onClick(v: View?) {
            Log.d("TestListToDetail", "A Game has been selected")
            callbacks!!.onGameSelected(game.id)
        }
    }

    private inner class GameAdapter(var games: List<Game>) :
            RecyclerView.Adapter<GameHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
            val view = layoutInflater.inflate(R.layout.item_game, parent, false)
            return GameHolder(view)
        }

        override fun getItemCount(): Int = games.size

        override fun onBindViewHolder(holder: GameHolder, position: Int) {
            val game = games[position]
            holder.bind(game)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

}


