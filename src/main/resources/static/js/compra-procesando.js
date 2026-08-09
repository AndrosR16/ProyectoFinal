/*
 * Overlay de "procesando compra": intercepta el envío del formulario de
 * checkout para mostrar una secuencia de pasos (01-04) antes de enviar de
 * verdad, y da micro-feedback inmediato al agregar productos al carrito.
 * Respeta prefers-reduced-motion saltando la secuencia escalonada.
 */
(function () {

    var DURACIONES_MS = [900, 1100, 900, 600];

    function avanzarPaso(pasos, barraFill, form, indice) {
        if (indice >= pasos.length) {
            form.submit();
            return;
        }

        var paso = pasos[indice];
        paso.classList.add("compra-paso-activo");

        setTimeout(function () {
            paso.classList.remove("compra-paso-activo");
            paso.classList.add("compra-paso-listo");

            if (barraFill) {
                barraFill.style.width = ((indice + 1) * (100 / pasos.length)) + "%";
            }

            avanzarPaso(pasos, barraFill, form, indice + 1);
        }, DURACIONES_MS[indice]);
    }

    function completarSinAnimacion(pasos, barraFill, form) {
        pasos.forEach(function (paso) {
            paso.classList.add("compra-paso-listo");
        });

        if (barraFill) {
            barraFill.style.width = "100%";
        }

        setTimeout(function () {
            form.submit();
        }, 250);
    }

    document.addEventListener("DOMContentLoaded", function () {

        var formCompra = document.querySelector("form.js-compra-form");

        if (formCompra) {
            formCompra.addEventListener("submit", function (evento) {
                if (!formCompra.checkValidity()) {
                    return;
                }

                evento.preventDefault();

                var boton = formCompra.querySelector("button[type='submit']");
                if (boton) {
                    boton.disabled = true;
                }

                var overlay = document.getElementById("compra-overlay");
                if (!overlay) {
                    formCompra.submit();
                    return;
                }

                overlay.hidden = false;
                overlay.setAttribute("aria-hidden", "false");

                var pasos = overlay.querySelectorAll(".compra-paso");
                var barraFill = overlay.querySelector(".compra-barra-fill");
                var prefiereMovimientoReducido = window.matchMedia
                    && window.matchMedia("(prefers-reduced-motion: reduce)").matches;

                if (prefiereMovimientoReducido) {
                    completarSinAnimacion(pasos, barraFill, formCompra);
                } else {
                    avanzarPaso(pasos, barraFill, formCompra, 0);
                }
            });
        }

        /* Al volver con "Atrás" desde la factura, Firefox/Safari restauran la
           página desde el bfcache con el overlay congelado encima. Hay que
           devolverla a su estado inicial o queda inusable. */
        window.addEventListener("pageshow", function (evento) {
            if (!evento.persisted || !formCompra) {
                return;
            }

            var overlay = document.getElementById("compra-overlay");

            if (overlay) {
                overlay.hidden = true;
                overlay.setAttribute("aria-hidden", "true");

                overlay.querySelectorAll(".compra-paso").forEach(function (paso) {
                    paso.classList.remove("compra-paso-activo", "compra-paso-listo");
                });

                var barraFill = overlay.querySelector(".compra-barra-fill");
                if (barraFill) {
                    barraFill.style.width = "0%";
                }
            }

            var boton = formCompra.querySelector("button[type='submit']");
            if (boton) {
                boton.disabled = false;
            }
        });

        document.querySelectorAll("form.producto-form").forEach(function (form) {
            form.addEventListener("submit", function () {
                if (!form.checkValidity()) {
                    return;
                }

                var boton = form.querySelector("button[type='submit']");
                if (!boton) {
                    return;
                }

                boton.classList.add("btn-cargando");

                setTimeout(function () {
                    boton.disabled = true;
                }, 0);
            });
        });
    });
})();
