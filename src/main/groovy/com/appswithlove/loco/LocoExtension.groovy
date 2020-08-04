package com.appswithlove.loco

class LocoExtension {
    public static final NAME = 'Loco'
    def locoBaseUrl = "https://localise.biz/api/export/locale"
    def apiKey
    def lang = ['en']
    def defLang = 'en'
    def resDir
    def placeholderPattern = null
    def hideComments = false
    def tags
    def fallbackLang = null
}
