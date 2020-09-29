package com.bignerdranch.android.basketballcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_basketball.*
import java.util.*

private const val KEY_SCORE_A = "scoreA"
private const val KEY_SCORE_B = "scoreB"
private const val KEY_SCORE_DIFF = "scoreDifference"
private const val REQUEST_CODE_0 = 0
private const val ARG_GAME_ID = "game_id"

class BasketballFragment : Fragment() {
    /**
     * interface for hosting activities
     */
    interface Callbacks {
        fun onDisplayClicked()
    }

    private lateinit var basketball: Basketball
    private lateinit var plus3TeamAButton: Button
    private lateinit var plus2TeamAButton: Button
    private lateinit var plus1TeamAButton: Button
    private lateinit var plus3TeamBButton: Button
    private lateinit var plus2TeamBButton: Button
    private lateinit var plus1TeamBButton: Button
    private lateinit var resetButton: Button
    private lateinit var statButton: Button
    private lateinit var scoreATextView: TextView
    private lateinit var scoreBTextView: TextView
    private lateinit var scoreDifferenceTextView: TextView
    private lateinit var teamATextView: TextView
    private lateinit var teamBTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var game: LiveData<Game>
    private var callbacks: Callbacks? = null
    private var startTime:Long = 0
    private var endTime:Long = 0

    private val basketballViewModel: BasketballViewModel by lazy {
        ViewModelProviders.of(this).get(BasketballViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    companion object {
        fun newInstance(gameId: UUID): BasketballFragment {
            val args = Bundle().apply {
                putSerializable(ARG_GAME_ID, gameId)
            }
            return BasketballFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId: UUID = arguments?.getSerializable(ARG_GAME_ID) as UUID
        basketballViewModel.loadGame(gameId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  inflater.inflate(R.layout.fragment_basketball, container, false)

        plus3TeamAButton = view.findViewById(R.id.plus3_button_teamA) as Button
        plus2TeamAButton = view.findViewById(R.id.plus2_button_teamA) as Button
        plus1TeamAButton = view.findViewById(R.id.plus1_button_teamA) as Button
        plus3TeamBButton = view.findViewById(R.id.plus3_button_teamB) as Button
        plus2TeamBButton = view.findViewById(R.id.plus2_button_teamB) as Button
        plus1TeamBButton = view.findViewById(R.id.plus1_button_teamB) as Button
        statButton = view.findViewById(R.id.stat_button) as Button
        resetButton = view.findViewById(R.id.reset_button) as Button
        saveButton = view.findViewById(R.id.save_button) as Button
        scoreATextView = view.findViewById(R.id.team_a_score) as TextView
        scoreBTextView = view.findViewById(R.id.team_b_score) as TextView
        scoreDifferenceTextView = view.findViewById(R.id.score_difference) as TextView
        teamATextView = view.findViewById(R.id.team_A) as TextView
        teamBTextView = view.findViewById(R.id.team_b) as TextView
        teamATextView.text = basketballViewModel.teamAName
        teamBTextView.text = basketballViewModel.teamBName
        scoreATextView.text = basketballViewModel.scoreA.toString()
        scoreBTextView.text = basketballViewModel.scoreB.toString()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        basketballViewModel.gameLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { game ->
                game?.let {
                    this.game = basketballViewModel.gameLiveData
                    teamATextView.text = this.game.value!!.teamAName
                    teamBTextView.text = this.game.value!!.teamBName
                    scoreATextView.text = this.game.value!!.teamAScore.toString()
                    scoreBTextView.text = this.game.value!!.teamBScore.toString()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        plus3TeamAButton.setOnClickListener {
            scoreATextView.text = (basketballViewModel.updatePoints("A", 3).toString())
        }

        plus2TeamAButton.setOnClickListener{
            scoreATextView.text = (basketballViewModel.updatePoints("A", 2).toString())
        }

        plus1TeamAButton.setOnClickListener{
            scoreATextView.text = (basketballViewModel.updatePoints("A", 1).toString())
        }

        plus3TeamBButton.setOnClickListener{
            scoreBTextView.text = (basketballViewModel.updatePoints("B", 3).toString())
        }

        plus2TeamBButton.setOnClickListener{
            scoreBTextView.text = (basketballViewModel.updatePoints("B", 2).toString())
        }

        plus1TeamBButton.setOnClickListener{
            scoreBTextView.text = (basketballViewModel.updatePoints("B", 1).toString())
        }


        statButton.setOnClickListener{
            val teamAscore = basketballViewModel.scoreA
            val teamBscore = basketballViewModel.scoreB
            val context = this.context
            val intent = MainActivity2.newIntent(
                context!!,
                teamAScore = teamAscore,
                teamBScore = teamBscore
            )
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_CODE_0)
        }


        resetButton.setOnClickListener{
            basketballViewModel.resetScore()
            scoreATextView.text = "0"
            scoreBTextView.text = "0"
        }

        displayButton.setOnClickListener{
            Log.d("testDetailToList", "Display button Pressed")
            callbacks?.onDisplayClicked()
        }

        saveButton.setOnClickListener {
            Log.d("save", "Display button Pressed")
            basketballViewModel.saveGame(this.game.value!!)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data); // comment this unless you want to pass your result to the activity.
         if (resultCode != Activity.RESULT_OK){
             return
         }

        if (requestCode == REQUEST_CODE_0){
            if (data != null) {
                basketballViewModel.scoreDifference = data.getIntExtra(EXTRA_SCORE_DIFF, 0)
                scoreDifferenceTextView.text = basketballViewModel.scoreDifference.toString()
            }
        }
    }
}

