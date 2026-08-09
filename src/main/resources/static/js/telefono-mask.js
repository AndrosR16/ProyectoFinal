/*
 * Máscara de teléfono costarricense: formatea la entrada como 8888-7777
 * a medida que se escribe (4 dígitos, guion, 4 dígitos). Solo dígitos.
 * También formatea el valor inicial al cargar, porque desde la base de
 * datos el número llega sin guion (se almacena a 8 dígitos, sin separadores).
 */
(function () {

    function formatearTelefono(valor) {
        var digitos = valor.replace(/\D/g, "").slice(0, 8);
        var partes = [];

        partes.push(digitos.slice(0, 4));
        if (digitos.length > 4) {
            partes.push(digitos.slice(4, 8));
        }

        return partes.filter(Boolean).join("-");
    }

    document.addEventListener("DOMContentLoaded", function () {

        document.querySelectorAll(".js-telefono").forEach(function (input) {
            input.value = formatearTelefono(input.value);

            input.addEventListener("input", function () {
                input.value = formatearTelefono(input.value);
            });
        });
    });
})();
