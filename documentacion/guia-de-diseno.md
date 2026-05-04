# Guía de Diseño — Sistema de Gestión de Biblioteca Universitaria

> **Versión:** 1.0  
> **Framework UI:** PrimeFaces 13 + Jakarta Faces 4.0  
> **Enfoque:** Profesional, moderno, accesible. Inspirado en sistemas de diseño como Material Design 3 y bibliotecas universitarias contemporáneas.

---

## 1. Paleta de Colores

Inspirada en bibliotecas modernas: madera cálida, papel, tinta, acentos académicos.

### 1.1 Colores Principales

| Token | Hex | Uso |
|---|---|---|
| `--color-primary` | `#1B3A4B` | Navbar, sidebar, botones principales, títulos de sección |
| `--color-primary-hover` | `#15303E` | Hover de botones primarios |
| `--color-primary-light` | `#E8F0F4` | Fondos sutiles, filas alternadas de tabla |
| `--color-secondary` | `#C9A96E` | Acentos, badges, iconos activos, links |
| `--color-secondary-light` | `#F5EFE1` | Fondos de tarjetas destacadas, hover de filas |

### 1.2 Colores de Superficie

| Token | Hex | Uso |
|---|---|---|
| `--color-surface` | `#FFFFFF` | Fondo de tarjetas, paneles, formularios |
| `--color-background` | `#F5F3EF` | Fondo general de página |
| `--color-sidebar` | `#0F2837` | Sidebar y navbar (más oscuro que el primary) |

### 1.3 Colores de Texto

| Token | Hex | Uso |
|---|---|---|
| `--color-text-primary` | `#1A1A1A` | Texto principal, títulos |
| `--color-text-secondary` | `#5C5C5C` | Subtítulos, metadatos, labels |
| `--color-text-muted` | `#9E9E9E` | Placeholders, texto deshabilitado |
| `--color-text-on-dark` | `#FFFFFF` | Texto sobre fondos oscuros (sidebar, navbar) |

### 1.4 Colores Semánticos

| Token | Hex | Uso |
|---|---|---|
| `--color-success` | `#2D6A4F` | Disponible, devuelto a tiempo |
| `--color-warning` | `#B8860B` | Vencido, por vencer |
| `--color-danger` | `#A42834` | Penalizado, agotado, acciones destructivas |
| `--color-info` | `#1B6B93` | Notificaciones, badges informativos |

### 1.5 Modo Oscuro (Opcional — Fase 2)

| Token | Hex |
|---|---|
| `--dark-surface` | `#1E1E1E` |
| `--dark-background` | `#121212` |
| `--dark-text` | `#E0E0E0` |

---

## 2. Tipografía

### 2.1 Familias

| Rol | Familia | Fallback |
|---|---|---|
| Texto general (UI) | `Inter` | `system-ui, -apple-system, sans-serif` |
| Títulos / Headings | `Playfair Display` | `Georgia, serif` |
| Código / Datos | `JetBrains Mono` | `Consolas, monospace` |

**Carga desde Google Fonts:**
```html
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
```

### 2.2 Escala Tipográfica

| Nivel | Etiqueta / Uso | Font | Size | Weight | Line-height |
|---|---|---|---|---|---|
| H1 | Título de página | Playfair Display | 32px | 700 | 1.2 |
| H2 | Título de sección | Playfair Display | 24px | 600 | 1.3 |
| H3 | Título de tarjeta | Inter | 18px | 600 | 1.4 |
| Body L | Texto general | Inter | 16px | 400 | 1.6 |
| Body M | Texto secundario, tablas | Inter | 14px | 400 | 1.5 |
| Body S | Metadatos, badges, labels | Inter | 12px | 500 | 1.4 |
| Caption | Notas al pie, copyright | Inter | 11px | 400 | 1.4 |

---

## 3. Espaciado

Sistema basado en múltiplos de 4px.

| Token | Valor | Uso |
|---|---|---|
| `--space-xs` | 4px | Gap mínimo entre icono y texto |
| `--space-sm` | 8px | Padding interno de badges, chips |
| `--space-md` | 16px | Padding de tarjetas, celdas de tabla |
| `--space-lg` | 24px | Separación entre secciones |
| `--space-xl` | 32px | Márgenes de página, separación de bloques grandes |
| `--space-2xl` | 48px | Hero sections, espaciado de layout |

---

## 4. Bordes y Sombras

| Token | Valor |
|---|---|
| `--radius-sm` | 6px |
| `--radius-md` | 10px |
| `--radius-lg` | 16px |
| `--shadow-card` | `0 2px 8px rgba(0,0,0,0.06)` |
| `--shadow-card-hover` | `0 4px 16px rgba(0,0,0,0.10)` |
| `--shadow-nav` | `0 2px 12px rgba(0,0,0,0.08)` |

---

## 5. Layout General

### 5.1 Estructura Base

```
┌──────────────────────────────────────────────┐
│  TOPBAR — logo, búsqueda, avatar, logout     │ altura: 60px
├────────┬─────────────────────────────────────┤
│        │                                     │
│SIDEBAR │        CONTENIDO PRINCIPAL          │
│ 220px  │        max-width: 1100px            │
│        │        centrado                     │
│        │                                     │
├────────┴─────────────────────────────────────┤
│  FOOTER — copyright, links                   │ altura: 40px
└──────────────────────────────────────────────┘
```

- **Topbar:** `#0F2837` (azul muy oscuro), texto blanco, altura 60px
- **Sidebar:** `#0F2837`, ancho 220px colapsable a 60px, iconos + labels
- **Contenido:** `#F5F3EF` fondo general, tarjetas blancas con sombra
- **Footer:** `#1B3A4B`, centrado, solo texto de copyright

### 5.2 Páginas

| Página | Layout |
|---|---|
| Inicio (`index.xhtml`) | Hero section centrada con logo, sin sidebar |
| Login | Tarjeta centrada 420px, fondo con gradiente sutil |
| Catálogo | Sidebar con filtros + grid de tarjetas de libros |
| Mis Préstamos | Lista tipo timeline vertical con estados de color |
| Admin CRUD | DataTable con toolbar de acciones, modales para formularios |
| Admin Devoluciones | Tabla con badges de estado, botones de acción rápida |

---

## 6. Componentes

### 6.1 Tarjeta de Libro (Catálogo)

```
┌──────────────────────┐
│  📖                  │  ← icono o portada placeholder
│                      │
│  Título del Libro    │  ← Inter 16px bold
│  Autor               │  ← Inter 14px, color-secondary-text
│  ⬤ Disponible        │  ← badge success / danger
│                      │
│  [Solicitar]         │  ← botón primario, ancho completo
└──────────────────────┘
```

- Width: 240px, gap: 16px, grid responsive 1-4 columnas
- Sombra suave, hover eleva la sombra
- Badge de disponibilidad: verde si disponible, rojo si agotado

### 6.2 Badges de Estado (Préstamos)

| Estado | Color de fondo | Color de texto | Icono |
|---|---|---|---|
| ACTIVO | `#E8F5E9` | `#2D6A4F` | ● |
| VENCIDO | `#FFF3CD` | `#B8860B` | ⏰ |
| DEVUELTO | `#E3F2FD` | `#1B6B93` | ✓ |
| PENALIZADO | `#FDE8E8` | `#A42834` | ⚠ |

### 6.3 Formularios

- Labels arriba del input (no al costado)
- Inputs con borde `#D0D0D0`, focus `#1B3A4B`
- Altura de input: 44px (accesible en mobile)
- Placeholder en `#9E9E9E`
- Mensajes de error debajo del campo, en rojo, 12px

### 6.4 Botones

| Tipo | Fondo | Texto | Borde | Uso |
|---|---|---|---|---|
| Primary | `#1B3A4B` | Blanco | — | Acción principal |
| Secondary | Blanco | `#1B3A4B` | `#1B3A4B` | Acción secundaria |
| Success | `#2D6A4F` | Blanco | — | Confirmar, guardar |
| Danger | `#A42834` | Blanco | — | Eliminar, cancelar |
| Ghost | Transparente | `#1B3A4B` | — | Navegación, links |

- Altura: 40px
- Padding horizontal: 20px
- Border-radius: 8px
- Hover: oscurece 10%
- Transición: 150ms ease

### 6.5 DataTable

- Header: `#1B3A4B`, texto blanco, 12px bold uppercase
- Filas alternadas: `#F5F3EF` cada 2da fila
- Hover de fila: `#F5EFE1` (secundario claro)
- Padding de celda: 12px 16px
- Paginador centrado debajo de la tabla

---

## 7. Iconografía

Usar PrimeIcons (incluidos en PrimeFaces) con las siguientes convenciones:

| Contexto | Icono |
|---|---|
| Libro / Catálogo | `pi pi-book` |
| Usuario / Perfil | `pi pi-user` |
| Préstamo / Solicitar | `pi pi-shopping-cart` |
| Devolución | `pi pi-undo` |
| Login | `pi pi-sign-in` |
| Logout | `pi pi-sign-out` |
| Admin / Config | `pi pi-cog` |
| Dashboard / Inicio | `pi pi-home` |
| Buscar | `pi pi-search` |
| Agregar | `pi pi-plus` |
| Editar | `pi pi-pencil` |
| Eliminar | `pi pi-trash` |
| Disponible (éxito) | `pi pi-check-circle` |
| Agotado (error) | `pi pi-times-circle` |
| Vencido (warning) | `pi pi-exclamation-triangle` |
| Notificación | `pi pi-bell` |
| Email | `pi pi-envelope` |

---

## 8. Animaciones y Transiciones

- Transiciones de hover: `150ms ease`
- Transiciones de página (si se usa SPA via JSF): `250ms ease-in-out`
- Loaders: skeleton screens (PrimeFaces `p:skeleton`) en lugar de spinners
- Feedback de acciones: mensajes toast en esquina superior derecha, 4s de duración

---

## 9. Accesibilidad

- Contraste mínimo ratio 4.5:1 para texto normal
- Contraste mínimo ratio 3:1 para texto grande (>18px)
- Todos los inputs con `label` asociado
- Navegación por teclado completa
- Mensajes de error descriptivos (no genéricos)
- Focus visible en todos los elementos interactivos (outline `#1B3A4B`)

---

## 10. Responsive Breakpoints

| Breakpoint | Ancho | Layout |
|---|---|---|
| Mobile | < 768px | Sidebar oculto (toggle), tarjetas 1 columna, tabla scroll horizontal |
| Tablet | 768px – 1024px | Sidebar colapsado, tarjetas 2 columnas |
| Desktop | > 1024px | Sidebar expandido, tarjetas 3-4 columnas, tabla completa |

---

## 11. Implementación Técnica

### 11.1 CSS Personalizado

Crear archivo `src/main/webapp/resources/css/estilos.css` con todas las variables y estilos globales. Cargarlo en todas las páginas via:

```xml
<h:outputStylesheet library="css" name="estilos.css"/>
```

### 11.2 Configuración del Tema PrimeFaces

En `web.xml`, cambiar el tema:

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>arya</param-value>  <!-- temas disponibles: saga, vela, arya, luna, nova, rhea -->
</context-param>
```

### 11.3 Layout PrimeFaces

Usar el componente `<p:layout>` para la estructura sidebar + contenido. Evitar layouts manuales con divs.

---

> **Próximo paso:** Implementar este diseño sobre las vistas XHTML existentes usando PrimeFaces Layout + CSS personalizado.
