package com.appswithlove.loco

class LocoConfig {
    def locoBaseUrl = "https://localise.biz/api/export/locale"
    def apiKey
    def lang = ['en']
    def defLang = 'en'
    def resDir
    def fileName = "strings"
    def replace = [:]
    def placeholderPattern = null
    boolean hideComments = false
    def tags
    def fallbackLang = null
    boolean orderByAssetId = false
    def status
    boolean saveDefLangDuplicate = false
    def resourceNamePrefix = null
    boolean ignoreMissingTranslationWarnings = false
    def index
}
