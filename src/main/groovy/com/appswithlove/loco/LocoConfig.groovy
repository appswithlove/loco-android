package com.appswithlove.loco

class LocoConfig {
    def locoBaseUrl = "https://localise.biz/api/export/locale"
    def apiKey
    def lang = ['en']
    def defLang = 'en'
    def resDir
    def fileName = "strings"
    def placeholderPattern = null
    def hideComments = false
    def tags
    def fallbackLang = null
    def orderByAssetId = false
    def status
}
