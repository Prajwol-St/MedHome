package com.example.medhomeapp.network

object ApiConstants {

    const val USDA_BASE_URL = "https://api.nal.usda.gov/fdc/v1/"

    const val USDA_API_KEY = "D7tRgFaj5RDrooear0cJo8Yt4ZuQrXBs44knBfyg"

    const val SEARCH_FOODS = "foods/search"
    const val GET_FOOD_DETAILS = "food/{fdcId}"

    const val DEFAULT_PAGE_SIZE = 10
    const val MAX_PAGE_SIZE = 50

    const val DATA_TYPE_FOUNDATION = "Foundation"
    const val DATA_TYPE_SR_LEGACY = "SR Legacy"
    const val DATA_TYPE_BRANDED = "Branded"
}