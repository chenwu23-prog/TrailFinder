package com.example.trailfinder

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var avatarImageView: ImageView
    private lateinit var saveButton: Button

    private var currentUser: UserEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        usernameEditText = view.findViewById(R.id.profileUsername)
        avatarImageView = view.findViewById(R.id.profileAvatar)
        saveButton = view.findViewById(R.id.saveProfileButton)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(requireContext())
        val userDao = db.userDao()

        viewLifecycleOwner.lifecycleScope.launch {
            // 🟢 Load existing user (if any)
            currentUser = withContext(Dispatchers.IO) {
                userDao.getUser()
            }

            // 🟢 If no user yet, create one-time guest record
            if (currentUser == null) {
                currentUser = UserEntity(username = "Guest")
                withContext(Dispatchers.IO) { userDao.insertUser(currentUser!!) }
            }

            // 🟢 Update UI
            usernameEditText.setText(currentUser?.username ?: "Guest")
            currentUser?.avatarUri?.let { avatarImageView.setImageURI(Uri.parse(it)) }

            // 🟢 Save button handler
            saveButton.setOnClickListener {
                val updatedUser = currentUser?.copy(
                    username = usernameEditText.text.toString(),
                    avatarUri = currentUser?.avatarUri
                )

                if (updatedUser != null) {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        userDao.insertUser(updatedUser)
                    }

                    currentUser = updatedUser
                    Toast.makeText(requireContext(), "Profile saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
