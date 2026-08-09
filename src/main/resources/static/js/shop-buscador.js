/*
 * Filtra las tarjetas de la tienda en vivo según el texto ingresado,
 * ignorando mayúsculas y tildes. Oculta la columna completa de cada
 * producto que no coincide y muestra un aviso cuando no hay resultados.
 */
(function () {

    function normalizar(texto) {
        return texto
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }

    document.addEventListener("DOMContentLoaded", function () {

        var input = document.querySelector(".js-buscador-productos");
        var grid = document.getElementById("shopGrid");
        var sinResultados = document.querySelector(".js-shop-sin-resultados");

        if (!input || !grid) {
            return;
        }

        var columnas = Array.prototype.slice.call(
            grid.querySelectorAll(".js-producto-col"));

        input.addEventListener("input", function () {
            var termino = normalizar(input.value.trim());
            var visibles = 0;

            columnas.forEach(function (columna) {
                var coincide = termino === "" ||
                    normalizar(columna.textContent).indexOf(termino) !== -1;

                columna.hidden = !coincide;

                if (coincide) {
                    visibles++;
                }
            });

            if (sinResultados) {
                sinResultados.hidden = !(visibles === 0 && termino !== "");
            }
        });
    });
})();
