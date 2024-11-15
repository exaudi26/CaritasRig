import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.superbgoal.caritasrig.data.fetchBuildsWithAuth
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager

class BuildViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)
    private val _buildTitle = MutableLiveData<String>()
    val buildTitle: LiveData<String> get() = _buildTitle

    private val _components = MutableLiveData<Map<String, String?>>()
    val components: LiveData<Map<String, String?>> get() = _components

    private val _buildData = MutableLiveData<Build?>()
    val buildData: LiveData<Build?> get() = _buildData

    private val _componentDetail = MutableLiveData<String?>()
    val componentDetail: LiveData<String?> get() = _componentDetail

    init {
        // Load buildTitle from SharedPreferences
        val savedBuildTitle = sharedPreferences.getString("buildTitle", "") ?: ""
        _buildTitle.value = savedBuildTitle

        // Set buildTitle to BuildManager
        BuildManager.setBuildTitle(savedBuildTitle)
        Log.d("BuildViewModel", "Build Title Loaded and Set: $savedBuildTitle")

        fetchBuildByTitle(savedBuildTitle)
    }

    fun saveBuildTitle(title: String) {
        _buildTitle.value = title
        sharedPreferences.edit().putString("buildTitle", title).apply()
        BuildManager.setBuildTitle(title)
        Log.d("BuildViewModel", "Build Title Saved and Set: $title")
    }

    fun setBuildData(build: Build) {
        _buildData.value = build
        saveBuildTitle(build.title)
        updateComponentDetail(build)

        build.components?.let { components ->
            _components.value = mapOf(
                "CPU" to components.processor?.let { "Processor: ${it.name}, Cores: ${it.core_count}, ${it.core_clock} GHz" },
                "Case" to components.casing?.let { "Case: ${it.name}, Type: ${it.type}" },
                "GPU" to components.videoCard?.let { "GPU: ${it.name}, Memory: ${it.memory} GB" },
                "Motherboard" to components.motherboard?.let { "Motherboard: ${it.name}, Chipset: ${it.formFactor}" },
                "RAM" to components.memory?.let { "RAM: ${it.name}, Size: ${it.pricePerGb} GB, Speed: ${it.speed} MHz" },
                "InternalHardDrive" to components.internalHardDrive?.let { "Storage: ${it.name}, Capacity: ${it.capacity} GB" },
                "PowerSupply" to components.powerSupply?.let { "PSU: ${it.name}, Wattage: ${it.wattage} W" },
                "CPU Cooler" to components.cpuCooler?.let { "CPU Cooler: ${it.name}, Fan Speed: ${it.rpm} RPM" },
                "Headphone" to components.headphone?.let { "Headphone: ${it.name}, Type: ${it.type}" },
                "Keyboard" to components.keyboard?.let { "Keyboard: ${it.name}, Type: ${it.switches}" },
                "Mouse" to components.mouse?.let { "Mouse: ${it.name}, Max DPI: ${it.maxDpi}" }
            )
        }

        Log.d("BuildViewModel", "Components data updated.")
    }


    private fun updateComponentDetail(build: Build) {
        build.components?.let { components ->
            val detail = buildString {
                components.processor?.let {
                    append("CPU: ${it.name}, ${it.core_count} cores, ${it.core_clock} GHz\n")
                }
                components.casing?.let {
                    append("Case: ${it.name}, Type: ${it.type}\n")
                }
                components.videoCard?.let {
                    append("GPU: ${it.name}, ${it.memory} GB\n")
                }
                components.motherboard?.let {
                    append("Motherboard: ${it.name}, Chipset: ${it.formFactor}\n")
                }
                components.memory?.let {
                    append("RAM: ${it.name}, ${it.speed} GB, ${it.speed} MHz\n")
                }
                components.internalHardDrive?.let {
                    append("Storage: ${it.name}, Capacity: ${it.capacity} GB\n")
                }
                components.powerSupply?.let {
                    append("powerSupply: ${it.name}, ${it.wattage} W\n")
                }
                components.cpuCooler?.let {
                    append("CPU Cooler: ${it.name}, Fan Speed: ${it.rpm} RPM\n")
                }
                components.headphone?.let {
                    append("Headphone: ${it.name}, Type: ${it.type}\n")
                }
                components.keyboard?.let {
                    append("Keyboard: ${it.name}, Type: ${it.switches}\n")
                }
                components.mouse?.let {
                    append("Mouse: ${it.name}, Max DPI: ${it.maxDpi}\n")
                }
            }
            _componentDetail.value = detail
            Log.d("BuildViewModel", "Component Detail Updated: $detail")
        } ?: run {
            _componentDetail.value = null
            Log.d("BuildViewModel", "Components are null or empty.")
        }
    }


    fun refreshBuildData() {
        _buildData.value?.let { currentBuild ->
            val refreshedBuild = currentBuild.copy(
                title = currentBuild.title, // Mempertahankan title saat ini
                components = currentBuild.components // Mempertahankan komponen yang ada
            )
            setBuildData(refreshedBuild) // Memperbarui data build dengan instance baru
            Log.d("BuildViewModel", "Build Data Refreshed: $refreshedBuild")
        } ?: run {
            Log.w("BuildViewModel", "No Build Data to Refresh")
        }
    }

    private fun fetchBuildByTitle(title: String) {
        fetchBuildsWithAuth(
            onSuccess = { builds ->
                val build = builds.find { it.title == title }
                if (build != null) {
                    setBuildData(build)
                    Log.d("BuildViewModel", "Build Data Fetched by Title: $build")
                } else {
                    Log.w("BuildViewModel", "No Build Found with Title: $title")
                }
            },
            onFailure = { errorMessage ->
                Log.e("BuildViewModel", "Error fetching build by title: $errorMessage")
            }
        )
    }


}





