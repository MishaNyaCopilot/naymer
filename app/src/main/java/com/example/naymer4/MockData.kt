package com.example.naymer4

object MockData {
    val initialAnnouncements = listOf(
        Announcement(
            title = "Разработка приложений",
            price = "50000 руб",
            category = "Программист",
            geo = "Москва",
            workingTime = "09:00-18:00",
            isUserAd = false
        ),
        Announcement(
            title = "Ремонт сантехники",
            price = "3000 руб",
            category = "Сантехник",
            geo = "СПб",
            workingTime = "08:00-20:00",
            isHot = true,
            isUserAd = false
        ),
        Announcement(
            title = "Создание сайтов",
            price = "40000 руб",
            category = "Программист",
            geo = "Новосибирск",
            workingTime = "10:00-19:00",
            isUserAd = true
        ),
        Announcement(
            title = "Замена проводки",
            price = "7000 руб",
            category = "Электрик",
            geo = "Екатеринбург",
            workingTime = "09:00-21:00",
            isHot = true,
            isUserAd = true
        ),
        Announcement(
            title = "Настройка серверов",
            price = "60000 руб",
            category = "Программист",
            geo = "Москва",
            workingTime = "11:00-20:00",
            isUserAd = true
        ),
        Announcement(
            title = "Монтаж водопровода",
            price = "15000 руб",
            category = "Сантехник",
            geo = "Казань",
            workingTime = "08:00-18:00",
            isHot = true,
            isUserAd = true
        ),
        Announcement(
            title = "Подключение электрощита",
            price = "5000 руб",
            category = "Электрик",
            geo = "Челябинск",
            workingTime = "07:00-19:00",
            isUserAd = true
        ),
        Announcement(
            title = "Услуги электрика",
            price = "2000 руб",
            category = "Электрик",
            geo = "Омск",
            workingTime = "09:00-17:00",
            isHot = true,
            isUserAd = true
        )
    )
}
