import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.DoctorAvailabilityRepo
import com.example.medhomeapp.viewmodel.DoctorAvailabilityViewModel

class DoctorAvailabilityViewModelFactory(
    private val repo: DoctorAvailabilityRepo,
    private val doctorId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorAvailabilityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorAvailabilityViewModel(repo, doctorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}