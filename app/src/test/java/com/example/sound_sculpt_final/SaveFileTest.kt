import com.example.sound_sculpt_final.SaveFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SaveFileTest {

    @Mock
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var saveFile: SaveFile

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val user = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(user.uid).thenReturn("testUserId")
        Mockito.`when`(firebaseAuth.currentUser).thenReturn(user)

        saveFile = SaveFile()
        saveFile.userId = "testUserId"
        saveFile.max7DecibelArray = floatArrayOf(80.0f, 85.0f, 90.0f, 95.0f, 100.0f)
    }


    @Test
    fun testSaveToDevice_WithEmptyUserId_ShowToast() {
        saveFile.firebaseAuth = firebaseAuth
        saveFile.userId = "" // Setting an empty user ID
        saveFile.saveToDevice()
        // Assert that a toast message is shown indicating the user ID is not found
        Mockito.verify(saveFile).showToast("User ID not found")
    }

    @Test
    fun testSaveToDevice_WithEmptyDecibelArray_ShowToast() {
        saveFile.firebaseAuth = firebaseAuth
        saveFile.max7DecibelArray = floatArrayOf() // Setting an empty decibel array
        saveFile.saveToDevice()
        // Assert that a toast message is shown indicating the max decibel values are not found
        Mockito.verify(saveFile).showToast("Max decibel values not found")
    }

    // Add more tests as needed...

}
