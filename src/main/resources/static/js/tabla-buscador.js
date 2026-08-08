/*
 * Buscador interno de tablas: filtra las filas en vivo según el texto
 * ingresado, ignorando mayúsculas y tildes. Cada input con la clase
 * .js-buscador-tabla filtra la tabla de su mismo .contenedor-tabla.
 */
(function () {

    function normalizar(texto) {
        return texto
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }

    document.addEventListener("DOMContentLoaded", function () {

        document.querySelectorAll(".js-buscador-tabla").forEach(function (input) {

            var contenedor = input.closest(".contenedor-tabla");
            var tabla = contenedor ? contenedor.querySelector("table") : null;

            if (!tabla || !tabla.tBodies.length) {
                return;
            }

            var cuerpo = tabla.tBodies[0];
            var columnas = tabla.tHead
                ? tabla.tHead.rows[0].cells.length
                : cuerpo.rows[0].cells.length;

            var filaSinResultados = document.createElement("tr");
            filaSinResultados.className = "fila-sin-resultados";
            filaSinResultados.hidden = true;

            var celda = document.createElement("td");
            celda.colSpan = columnas;
            celda.className = "text-center dato-secundario";
            celda.textContent = input.dataset.mensajeVacio || "Sin coincidencias";
            filaSinResultados.appendChild(celda);
            cuerpo.appendChild(filaSinResultados);

            input.addEventListener("input", function () {
                var termino = normalizar(input.value.trim());
                var visibles = 0;

                Array.prototype.forEach.call(cuerpo.rows, function (fila) {
                    if (fila === filaSinResultados) {
                        return;
                    }

                    /* Las filas de estado vacío del servidor no se filtran */
                    if (fila.querySelector(".estado-vacio")) {
                        return;
                    }

                    var coincide = termino === "" ||
                        normalizar(fila.textContent).indexOf(termino) !== -1;

                    fila.hidden = !coincide;

                    if (coincide) {
                        visibles++;
                    }
                });

                filaSinResultados.hidden = !(visibles === 0 && termino !== "");
            });
        });
    });
})();
