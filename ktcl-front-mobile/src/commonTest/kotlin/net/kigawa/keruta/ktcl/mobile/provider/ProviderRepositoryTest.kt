package net.kigawa.keruta.ktcl.mobile.provider

import kotlin.test.Test
import kotlin.test.assertEquals
import net.kigawa.keruta.ktcl.mobile.msg.provider.Provider

class ProviderRepositoryTest {

    @Test
    fun testUpdateProviders() {
        val repository = ProviderRepository()
        
        val providers = listOf(
            Provider(
                id = 1,
                name = "Provider 1",
                issuer = "https://issuer1.example.com",
                audience = "audience1"
            ),
            Provider(
                id = 2,
                name = "Provider 2",
                issuer = "https://issuer2.example.com",
                audience = "audience2"
            )
        )
        
        repository.updateProviders(providers)
        
        assertEquals(2, repository.providers.value.size)
        assertEquals("Provider 1", repository.providers.value[0].name)
        assertEquals("https://issuer1.example.com", repository.providers.value[0].issuer)
    }

    @Test
    fun testAddProvider() {
        val repository = ProviderRepository()
        
        val provider1 = Provider(
            id = 1,
            name = "Provider 1",
            issuer = "https://issuer1.example.com",
            audience = "audience1"
        )
        val provider2 = Provider(
            id = 2,
            name = "Provider 2",
            issuer = "https://issuer2.example.com",
            audience = "audience2"
        )
        
        repository.addProvider(provider1)
        repository.addProvider(provider2)
        
        assertEquals(2, repository.providers.value.size)
    }

    @Test
    fun testProviderDataClass() {
        val provider = Provider(
            id = 1,
            name = "Test Provider",
            issuer = "https://test.example.com",
            audience = "test-audience"
        )
        
        assertEquals(1, provider.id)
        assertEquals("Test Provider", provider.name)
        assertEquals("https://test.example.com", provider.issuer)
        assertEquals("test-audience", provider.audience)
    }
}
