package pe.edu.upeu.pharmamobil.domain.platform

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

class CompartidorIos : Compartidor {
    override fun compartir(texto: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(texto),
            applicationActivities = null,
        )
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null,
        )
    }
}
