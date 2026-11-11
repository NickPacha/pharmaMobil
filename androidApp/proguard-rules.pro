# =====================================================================
# Reglas de ProGuard / R8 para PharmaMobil (Modo Release)
# Cumplimiento de especificación RES-MIN-01 y RNF-02
# =====================================================================

# 1. Conservar DTOs y modelos de serialización (kotlinx.serialization)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep,allowobfuscation,allowshrinking class * kotlinx.serialization.Serializer
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep class pe.edu.upeu.pharmamobil.data.remote.dto.** { *; }

# 2. Conservar módulos de Inyección de Dependencias con Koin
-keep class pe.edu.upeu.pharmamobil.di.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# 3. Conservar modelos del dominio y base de datos local SQLDelight
-keep class pe.edu.upeu.pharmamobil.domain.model.** { *; }
-keep class pe.edu.upeu.pharmamobil.db.** { *; }
-keep class app.cash.sqldelight.** { *; }

# 4. Conservar bibliotecas de red Ktor y Coroutines
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# 5. Soporte para Compose Multiplatform
-keep class androidx.compose.** { *; }
