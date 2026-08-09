# Fotos de productos

La tienda (`/shop`) muestra la foto de cada medicamento según su campo
"URL de imagen". Si la URL falla o está vacía, la tarjeta cae a un ícono de
respaldo (nunca se ve rota).

## Estado actual (demo)

Los 15 medicamentos sembrados ya traen **fotos reales** por categoría, servidas
por [LoremFlickr](https://loremflickr.com) (fotos de Flickr con licencia
Creative Commons, sin API key). Se cargan solas en el navegador; no hay que
tomar ninguna foto. Requiere conexión a internet del lado del visitante.

## Usar una foto propia

Dos formas de reemplazar la foto de un producto:

1. **Desde la app**: Medicamentos (admin) → Editar → campo "URL de imagen".
   Pegue cualquier URL (una foto externa, o una local como
   `/img/productos/acetaminofen.jpg`).
2. **Archivo local**: coloque la imagen en esta carpeta y ponga su ruta
   (`/img/productos/<archivo>`) en el campo "URL de imagen" del medicamento.
   Estilo recomendado: foto cenital sobre fondo claro, ~800x600, `.jpg`.
