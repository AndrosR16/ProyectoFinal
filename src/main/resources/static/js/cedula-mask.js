/*
 * Máscara de cédula costarricense: formatea la entrada como 0-0000-0000
 * a medida que se escribe (1 dígito, 4 dígitos, 4 dígitos). Solo dígitos.
 * Además muestra la nota de SINPE solo cuando ese método está elegido.
 */
(function () {

    function formatearCedula(valor) {
        var digitos = valor.replace(/\D/g, "").slice(0, 9);
        var partes = [];

        partes.push(digitos.slice(0, 1));
        if (digitos.length > 1) {
            partes.push(digitos.slice(1, 5));
        }
        if (digitos.length > 5) {
            partes.push(digitos.slice(5, 9));
        }

        return partes.filter(Boolean).join("-");
    }

    document.addEventListener("DOMContentLoaded", function () {

        document.querySelectorAll(".js-cedula").forEach(function (input) {
            input.addEventListener("input", function () {
                input.value = formatearCedula(input.value);
            });
        });

        var info = document.querySelector(".metodo-pago-info");
        var radios = document.querySelectorAll("input[name='metodoPago']");

        if (info && radios.length) {
            radios.forEach(function (radio) {
                radio.addEventListener("change", function () {
                    info.classList.toggle("d-none", radio.value !== "SINPE" || !radio.checked);
                });
            });
        }
    });
})();
