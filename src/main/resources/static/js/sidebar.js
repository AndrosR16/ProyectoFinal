/*
 * Sidebar colapsable: alterna entre ancho completo y riel de iconos.
 * La preferencia se guarda en localStorage y se aplica al cargar cada
 * página, antes de pintar el contenido, para evitar parpadeos.
 */
(function () {
    var CLAVE = "farmaciaSidebarColapsado";
    var raiz = document.documentElement;

    try {
        if (localStorage.getItem(CLAVE) === "1") {
            raiz.classList.add("sidebar-colapsado");
        }
    } catch (e) {
        /* localStorage bloqueado: se ignora, el sidebar queda expandido */
    }

    document.addEventListener("DOMContentLoaded", function () {
        var boton = document.getElementById("botonColapsarSidebar");

        if (!boton) {
            return;
        }

        boton.setAttribute(
            "aria-expanded",
            raiz.classList.contains("sidebar-colapsado") ? "false" : "true");

        boton.addEventListener("click", function () {
            var colapsado = raiz.classList.toggle("sidebar-colapsado");
            boton.setAttribute("aria-expanded", colapsado ? "false" : "true");

            try {
                localStorage.setItem(CLAVE, colapsado ? "1" : "0");
            } catch (e) {
                /* sin persistencia, el estado vive solo en esta página */
            }
        });
    });
})();
