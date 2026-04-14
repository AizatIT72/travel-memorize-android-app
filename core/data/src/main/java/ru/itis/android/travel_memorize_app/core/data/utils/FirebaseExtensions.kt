package ru.itis.android.travel_memorize_app.core.data.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

suspend fun <T> Task<T>.awaitResult(): T = await()
