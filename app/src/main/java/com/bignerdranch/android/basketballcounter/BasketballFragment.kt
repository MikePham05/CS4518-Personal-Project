package com.bignerdranch.android.basketballcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.AssetManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_basketball.*
import java.io.File
import java.util.*

private const val KEY_SCORE_A = "scoreA"
private const val KEY_SCORE_B = "scoreB"
private const val KEY_SCORE_DIFF = "scoreDifference"
private const val REQUEST_CODE_0 = 0
private const val ARG_GAME_ID = "game_id"
private const val REQUEST_PHOTO = 2

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
    private lateinit var teamAPhotoButton: ImageButton
    private lateinit var teamBPhotoButton: ImageButton
    private lateinit var teamAPhotoView: ImageView
    private lateinit var teamBPhotoView: ImageView
    private lateinit var teamAPhoto: File
    private lateinit var teamBPhoto: File
    private lateinit var photoUriA: Uri
    private lateinit var photoUriB: Uri
    private lateinit var cheeringSounds: CheeringSounds
    private var pictureATaken: Boolean = false
    private var pictureBTaken: Boolean = false
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
        cheeringSounds = CheeringSounds(requireContext().assets)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  inflater.inflate(R.layout.fragment_basketball, container, false)
        teamAPhotoButton = view.findViewById(R.id.team_A_camera) as ImageButton
        teamBPhotoButton = view.findViewById(R.id.team_B_camera) as ImageButton
        teamAPhotoView = view.findViewById(R.id.team_A_photo) as ImageView
        teamBPhotoView = view.findViewById(R.id.team_B_photo) as ImageView
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
                    teamAPhoto = basketballViewModel.getPhotoFile(game)
                    photoUriA = FileProvider.getUriForFile(
                            requireActivity(),
                            "com.bignerdranch.android.basketballcounter.fileprovider",
                            teamAPhoto)
                    teamBPhoto = basketballViewModel.getPhotoFile(game)
                    photoUriB = FileProvider.getUriForFile(
                            requireActivity(),
                            "com.bignerdranch.android.basketballcounter.fileprovider",
                            teamBPhoto
                        )
                    updatePhotoView()
                }
            }
        )
    }

    private fun updatePhotoView() {
        if (teamAPhoto.exists() and pictureATaken) {
            val bitmap = getScaledBitmap(teamAPhoto.path, requireActivity())
            teamAPhotoView.setImageBitmap(bitmap)
            pictureATaken = false
        }
        if (teamBPhoto.exists() and pictureBTaken) {
            val bitmap = getScaledBitmap(teamBPhoto.path, requireActivity())
            teamBPhotoView.setImageBitmap(bitmap)
            pictureBTaken = false
        }
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

        teamAPhotoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                pictureATaken = true
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUriA)

                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUriA,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                @Suppress("DEPRECATION")
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }

        teamBPhotoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                pictureBTaken = true
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUriB)

                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUriB,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                @Suppress("DEPRECATION")
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        requireActivity().revokeUriPermission(photoUriA, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        requireActivity().revokeUriPermission(photoUriB, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
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

        if (requestCode == REQUEST_PHOTO) {
            requireActivity().revokeUriPermission(photoUriA, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            requireActivity().revokeUriPermission(photoUriB, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            updatePhotoView()
        }
    }
}

