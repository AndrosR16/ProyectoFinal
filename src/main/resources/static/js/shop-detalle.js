/*
 * Detalle rápido de producto (quick view) tipo tienda: al tocar una tarjeta
 * abre un modal con foto grande, precio, stock, vendedor (ficticio),
 * descripción, botón de agregar al carrito y productos relacionados.
 * Todo el contenido se arma en el cliente a partir de los data-* de las
 * tarjetas ya renderizadas; no hay llamadas al servidor.
 */
(function () {

    var SELLERS = [
        "Farmacia Vida",
        "Droguería Central de Costa Rica",
        "Distribuidora Farmacéutica del Valle",
        "Laboratorios MediCR",
        "Corporación Médica Costarricense"
    ];

    function money(valor) {
        var n = Number(valor);
        if (isNaN(n)) {
            return "";
        }
        return "₡" + n.toLocaleString("es-CR", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    }

    function vendedorDe(id) {
        var n = parseInt(id, 10) || 0;
        return SELLERS[n % SELLERS.length];
    }

    document.addEventListener("DOMContentLoaded", function () {

        var modalEl = document.getElementById("modalProducto");
        if (!modalEl || typeof bootstrap === "undefined") {
            return;
        }

        var modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        var i18n = document.getElementById("shopI18n");
        var textos = i18n ? i18n.dataset : {};

        var foto = document.getElementById("mpFoto");
        var icono = document.getElementById("mpIcono");
        var nombre = document.getElementById("mpNombre");
        var precio = document.getElementById("mpPrecio");
        var stockCont = document.getElementById("mpStock");
        var vendedor = document.getElementById("mpVendedor");
        var descripcion = document.getElementById("mpDescripcion");
        var form = document.getElementById("mpForm");
        var cantidad = document.getElementById("mpCantidad");
        var agregar = document.getElementById("mpAgregar");
        var relacionados = document.getElementById("mpRelacionados");

        // Pool único de productos (las tarjetas destacadas se repiten en el grid)
        var pool = {};
        document.querySelectorAll(".producto-card").forEach(function (card) {
            var d = card.dataset;
            if (d.id && !pool[d.id]) {
                pool[d.id] = {
                    id: d.id,
                    nombre: d.nombre || "",
                    precio: d.precio || "",
                    stock: parseInt(d.stock, 10) || 0,
                    imagen: d.imagen || "",
                    descripcion: d.descripcion || "",
                    destacado: d.destacado === "true"
                };
            }
        });
        var poolArr = Object.keys(pool).map(function (k) { return pool[k]; });

        function pintarStock(cont, stock) {
            var clase, texto;
            if (stock <= 0) {
                clase = "producto-stock-agotado";
                texto = textos.stockAgotado || "Agotado";
            } else if (stock < 5) {
                clase = "producto-stock-pocas";
                texto = (textos.stockPocas || "Pocas unidades") + " (" + stock + ")";
            } else {
                clase = "producto-stock-ok";
                texto = textos.stockOk || "En stock";
            }
            cont.innerHTML = "";
            var span = document.createElement("span");
            span.className = "producto-stock " + clase;
            span.textContent = texto;
            cont.appendChild(span);
        }

        function pintarRelacionados(actual) {
            relacionados.innerHTML = "";
            poolArr
                .filter(function (p) { return p.id !== actual.id; })
                .slice(0, 4)
                .forEach(function (p) {
                    var card = document.createElement("button");
                    card.type = "button";
                    card.className = "mp-rel-card";

                    var img = "";
                    if (p.imagen) {
                        img = '<img src="' + p.imagen + '" alt="" loading="lazy" '
                            + 'onerror="this.remove()">';
                    }
                    card.innerHTML =
                        '<span class="mp-rel-img">' + img + '</span>'
                        + '<span class="mp-rel-nombre"></span>'
                        + '<span class="mp-rel-precio"></span>';
                    card.querySelector(".mp-rel-nombre").textContent = p.nombre;
                    card.querySelector(".mp-rel-precio").textContent = money(p.precio);

                    card.addEventListener("click", function () {
                        populate(p);
                        modalEl.querySelector(".modal-body").scrollTop = 0;
                    });

                    relacionados.appendChild(card);
                });
        }

        function populate(p) {
            nombre.textContent = p.nombre;
            precio.textContent = money(p.precio);
            pintarStock(stockCont, p.stock);
            vendedor.textContent = vendedorDe(p.id);
            descripcion.textContent = p.descripcion || "";

            if (p.imagen) {
                foto.src = p.imagen;
                foto.alt = p.nombre;
                foto.style.display = "";
            } else {
                foto.removeAttribute("src");
                foto.style.display = "none";
            }

            form.action = "/carrito/agregar/" + p.id;
            cantidad.value = "1";
            cantidad.max = p.stock;
            cantidad.disabled = p.stock <= 0;
            agregar.disabled = p.stock <= 0;

            pintarRelacionados(p);
        }

        function abrirDesde(card) {
            var p = pool[card.dataset.id];
            if (p) {
                populate(p);
                modal.show();
            }
        }

        document.querySelectorAll(".producto-card").forEach(function (card) {
            card.addEventListener("click", function (e) {
                if (e.target.closest(".producto-form")) {
                    return;
                }
                abrirDesde(card);
            });
            card.addEventListener("keydown", function (e) {
                if ((e.key === "Enter" || e.key === " ") && !e.target.closest(".producto-form")) {
                    e.preventDefault();
                    abrirDesde(card);
                }
            });
        });
    });
})();
