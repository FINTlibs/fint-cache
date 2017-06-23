package no.fint.cache

import no.fint.cache.utils.CacheUri
import no.fint.cache.utils.TestCacheService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = TestCacheService)
class FintCacheIntegrationSpec extends Specification {
    private final String cacheUri = CacheUri.create('rogfk.no', 'test')

    @Autowired
    private TestCacheService testCacheService

    void setup() {
        def cache = new FintCache<String>()
        cache.update(['test1', 'test2'])
        testCacheService.put(cacheUri, cache)
    }

    void cleanup() {
        testCacheService.remove(cacheUri)
    }

    def "Create cache"() {
        given:
        def uri = CacheUri.create('rogfk.no', 'test123')

        when:
        def cache = testCacheService.createCache(uri)

        then:
        testCacheService.remove(CacheUri.create('rogfk.no', 'test123'))
        cache != null
        testCacheService.getAll(uri).size() == 0
    }

    def "Get all values from cache"() {
        when:
        def values = testCacheService.getAll(cacheUri)

        then:
        values.size() == 2
        values.contains('test1')
        values.contains('test2')
    }

    def "Return empty list when no values are present in cache"() {
        given:
        testCacheService.flush(cacheUri)

        when:
        def values = testCacheService.getAll(cacheUri)

        then:
        values.size() == 0
    }

    def "Add items to cache"() {
        when:
        testCacheService.add(cacheUri, ['test3'])
        def values = testCacheService.getAll(cacheUri)

        then:
        values.size() == 3
    }

    def "Get all values since timestamp"() {
        when:
        def values = testCacheService.getAll(cacheUri, System.currentTimeMillis() - 500)

        then:
        values.size() == 2
        values.contains('test1')
        values.contains('test2')
    }

    def "Return no values when there are no updates since timestamp"() {
        when:
        def values = testCacheService.getAll(cacheUri, System.currentTimeMillis() + 500)

        then:
        values.size() == 0
    }

    def "Get last updated"() {
        when:
        def lastUpdated = testCacheService.getLastUpdated(cacheUri)

        then:
        lastUpdated < System.currentTimeMillis()
    }

    def "Update cache, add new value"() {
        when:
        testCacheService.update(cacheUri, ['test1', 'test2', 'test3'])
        def values = testCacheService.getAll(cacheUri)

        then:
        values.size() == 3
        values.contains('test3')
    }

    def "Get keys"() {
        when:
        def keys = testCacheService.getKeys()

        then:
        keys.size() == 1
        keys[0] == CacheUri.create('rogfk.no', 'test')
    }

    def "Flush cache"() {
        when:
        testCacheService.flush(cacheUri)
        def values = testCacheService.getAll(cacheUri)

        then:
        values.size() == 0
    }
}
