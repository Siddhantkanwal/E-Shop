package com.example.shopping.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopping.R
import com.example.shopping.firestore.FirestoreClass
import com.example.shopping.models.Constants
import com.example.shopping.models.GlideLoader
import com.example.shopping.models.User
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.jar.Manifest

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    // Instance of User data model class. We will initialize it later on.
    private lateinit var mUserDetails: User

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    private var mUserProfileImageURL: String = ""

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        // TODO Step 2: Once we receive the user details through intent make some changes to the code so user can complete his profile if he is from the login screen. If user is from settings screen he can edit the profile details.
        // START
        // If the profile is incomplete then user is from login screen and wants to complete the profile.
        if (mUserDetails.profileCompleted == 0) {
            // Update the title of the screen to complete profile.
            tv_title.text = resources.getString(R.string.title_complete_profile)

            // Here, the some of the edittext components are disabled because it is added at a time of Registration.
            et_first_name.isEnabled = false
            et_first_name.setText(mUserDetails.firstName)

            et_last_name.isEnabled = false
            et_last_name.setText(mUserDetails.lastName)

            et_email.isEnabled = false
            et_email.setText(mUserDetails.email)
        } else {

            // Call the setup action bar function.
            setupActionBar()

            // Update the title of the screen to edit profile.
            tv_title.text = resources.getString(R.string.title_edit_profile)

            // Load the image using the GlideLoader class with the use of Glide Library.
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, iv_user_photo)

            // Set the existing values to the UI and allow user to edit except the Email ID.
            et_first_name.setText(mUserDetails.firstName)
            et_last_name.setText(mUserDetails.lastName)

            et_email.isEnabled = false
            et_email.setText(mUserDetails.email)

            if (mUserDetails.mobile != 0L) {
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }
        }
        // END

        // Assign the on click event to the user profile photo.
        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        // Assign the on click event to the SAVE button.
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {

                    if (validateUserProfileDetails()) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null) {

                            FirestoreClass().uploadImageToCloudStorage(
                                this@UserProfileActivity,
                                mSelectedImageFileUri,
                                Constants.USER_PROFILE_IMAGE
                            )
                        } else {

                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {

                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this@UserProfileActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
                            iv_user_photo
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    // TODO Step 1: Create a function to setup action bar if the user is about to edit profile.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }
    // END

    /**
     * A function to validate the input entries for profile details.
     */
    private fun validateUserProfileDetails(): Boolean {
        return when {

            // We have kept the user profile picture is optional.
            // The FirstName, LastName, and Email Id are not editable when they come from the login screen.
            // The Radio button for Gender always has the default selected value.

            // Check if the mobile number is not empty as it is mandatory to enter.
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to update user profile details to the firestore.
     */
    private fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()

        // TODO Step 5: Update the code if user is about to Edit Profile details instead of Complete Profile.
        // Get the FirstName from editText and trim the space
        val firstName = et_first_name.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        // Get the LastName from editText and trim the space
        val lastName = et_last_name.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        // TODO Step 6: Email ID is not editable so we don't need to add it here to get the text from EditText.

        // Here we get the text from editText and trim the space
        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }
        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        // TODO Step 7: Update the code here if it is to edit the profile.
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        // Here if user is about to complete the profile then update the field or else no need.
        // 0: User profile is incomplete.
        // 1: User profile is completed.
        if (mUserDetails.profileCompleted == 0) {
            userHashMap[Constants.COMPLETE_PROFILE] = 1
        }
        // END

        // call the registerUser function of FireStore class to make an entry in the database.
        FirestoreClass().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )
    }

    /**
     * A function to notify the success result and proceed further accordingly after updating the user details.
     */
    fun userProfileUpdateSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()


        // TODO Step 8: Redirect it to the DashboardActivity instead of MainActivity.
        // Redirect to the Main Screen after profile completion.
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    /**
     * A function to notify the success result of image upload to the Cloud Storage.
     *
     * @param imageURL After successful upload the Firebase Cloud returns the URL.
     */
    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL = imageURL

        updateUserProfileDetails()
    }

}