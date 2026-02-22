package helpers

import groovy.json.JsonOutput

class JsonConfigHelper {

    static def getNestedValue(Map jsonData, String path) {
        List<String> keys = path.split('\\.') as List<String>
        def current = jsonData
        keys.each { key -> current = current[key] }
        return current.toString()
    }

    static def updateNestedValue(Map map, String path, String value) {
        List<String> keys = path.split('\\.') as List<String>
        Map current = map

        for (int i = 0; i < keys.size() - 1; i++) {
            current = current[keys[i]] as Map
        }
        current[keys[keys.size() - 1]] = value
    }

    static def saveJsonFile(File jsonFile, Map jsonData) {
        String json = JsonOutput.prettyPrint(JsonOutput.toJson(jsonData))
        jsonFile.text = json
        println "Saved JSON file: ${jsonFile.absolutePath}"
    }

    Map makeMutable(Map map) {
        Map mutableMap = new HashMap(map)
        mutableMap.each { key, val ->
            if (val instanceof Map) mutableMap[key] = makeMutable(val as Map)
        }
        return mutableMap
    }
}
