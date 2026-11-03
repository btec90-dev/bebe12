package com.example.network

data class NetworkContactPayload(
    val type: String,
    val name: String,
    val phone: String?,
    val address: String? = null
)

data class RemoteContactResponse(
    val id: String,
    val name: String,
    val phone: String?
)

data class CareDirectoryResponse(
    val doctors: List<RemoteContactResponse> = emptyList(),
    val pharmacies: List<RemoteContactResponse> = emptyList(),
    val clinics: List<RemoteContactResponse> = emptyList()
)

interface MamanNetworkService {
    suspend fun getFullCareDirectory(): CareDirectoryResponse
    suspend fun uploadNewContact(payload: NetworkContactPayload): RemoteContactResponse
}

class MockMamanNetworkService : MamanNetworkService {
    override suspend fun getFullCareDirectory(): CareDirectoryResponse {
        return CareDirectoryResponse(
            doctors = listOf(RemoteContactResponse("doc_1", "Dr. Sarah Jenkins", "555-0192")),
            pharmacies = listOf(RemoteContactResponse("phar_1", "City Center Pharmacy", "555-0183")),
            clinics = listOf(RemoteContactResponse("clin_1", "Maman & Bebe Care Clinic", "555-0174"))
        )
    }

    override suspend fun uploadNewContact(payload: NetworkContactPayload): RemoteContactResponse {
        return RemoteContactResponse(
            id = "rem_${System.currentTimeMillis()}",
            name = payload.name,
            phone = payload.phone
        )
    }
}
