/**
 * LogiTrack - Lógica del dashboard principal.
 */
document.addEventListener('DOMContentLoaded', () => {

  if (!API.isLoggedIn()) {
    window.location.href = 'index.html';
    return;
  }

  const isAdmin = API.getRole() === 'ADMIN';
  const userName = API.getUser();
  const roleBadge = isAdmin
    ? '<span class="tag tag-admin">ADMIN</span>'
    : '<span class="tag tag-empleado">EMPLEADO</span>';

  document.getElementById('userName').innerHTML = '<strong>' + userName + '</strong><br>' + roleBadge;

  // Ocultar pestaña de auditorías si no es admin
  if (!isAdmin) {
    document.getElementById('navAuditorias').classList.add('hidden');
  }

  // ---- Logout ----
  document.getElementById('btnLogout').addEventListener('click', () => {
    API.clearToken();
    window.location.href = 'index.html';
  });

  // ---- Navegación de pestañas ----
  const tabs = document.querySelectorAll('.sidebar-nav a[data-tab]');
  tabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
      e.preventDefault();
      const target = tab.dataset.tab;
      document.querySelectorAll('.tab-section').forEach(s => s.classList.remove('active'));
      document.getElementById(target).classList.add('active');
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      loadTab(target);
    });
  });

  // Cargar la primera pestaña
  loadTab('bodegas');

  // ============================================================
  //  BODEGAS
  // ============================================================
  async function loadBodegas() {
    try {
      const list = await API.bodegas.list();
      const tbody = document.getElementById('bodegasBody');
      tbody.innerHTML = list.map(b => `
        <tr>
          <td>${b.id}</td>
          <td>${b.nombre}</td>
          <td>${b.ubicacion || ''}</td>
          <td>${b.capacidad || ''}</td>
          <td>${b.encargado || ''}</td>
          <td>${adminActions('bodega', b.id)}</td>
        </tr>`).join('');
      bindEditDelete('bodega');
    } catch (err) { showError('bodegas', err.message); }
  }

  document.getElementById('btnNewBodega').addEventListener('click', () => {
    openBodegaModal();
  });

  function openBodegaModal(id, data) {
    document.getElementById('bodegaModalTitle').textContent = id ? 'Editar Bodega' : 'Nueva Bodega';
    document.getElementById('bodegaId').value = id || '';
    document.getElementById('bodegaNombre').value = data?.nombre || '';
    document.getElementById('bodegaUbicacion').value = data?.ubicacion || '';
    document.getElementById('bodegaCapacidad').value = data?.capacidad || '';
    document.getElementById('bodegaEncargado').value = data?.encargado || '';
    document.getElementById('bodegaModal').classList.add('show');
  }

  document.getElementById('bodegaForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('bodegaId').value;
    const data = {
      nombre: document.getElementById('bodegaNombre').value,
      ubicacion: document.getElementById('bodegaUbicacion').value,
      capacidad: parseInt(document.getElementById('bodegaCapacidad').value) || 0,
      encargado: document.getElementById('bodegaEncargado').value
    };
    try {
      if (id) { await API.bodegas.update(id, data); } else { await API.bodegas.create(data); }
      document.getElementById('bodegaModal').classList.remove('show');
      loadBodegas();
    } catch (err) { alert(err.message); }
  });

  // ============================================================
  //  PRODUCTOS
  // ============================================================
  async function loadProductos() {
    try {
      const list = await API.productos.list();
      const tbody = document.getElementById('productosBody');
      tbody.innerHTML = list.map(p => {
        const bajo = p.stock < 10 ? '<span class="badge-stock-bajo tag">STOCK BAJO</span>' : '';
        return `
        <tr>
          <td>${p.id}</td>
          <td>${p.nombre}</td>
          <td>${p.categoria || ''}</td>
          <td>${p.stock} ${bajo}</td>
          <td>$${p.precio}</td>
          <td>${adminActions('producto', p.id)}</td>
        </tr>`;
      }).join('');
      bindEditDelete('producto');
    } catch (err) { showError('productos', err.message); }
  }

  document.getElementById('btnNewProducto').addEventListener('click', () => {
    openProductoModal();
  });

  function openProductoModal(id, data) {
    document.getElementById('productoModalTitle').textContent = id ? 'Editar Producto' : 'Nuevo Producto';
    document.getElementById('productoId').value = id || '';
    document.getElementById('productoNombre').value = data?.nombre || '';
    document.getElementById('productoCategoria').value = data?.categoria || '';
    document.getElementById('productoStock').value = data?.stock ?? 0;
    document.getElementById('productoPrecio').value = data?.precio || '';
    document.getElementById('productoModal').classList.add('show');
  }

  document.getElementById('productoForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('productoId').value;
    const data = {
      nombre: document.getElementById('productoNombre').value,
      categoria: document.getElementById('productoCategoria').value,
      stock: parseInt(document.getElementById('productoStock').value) || 0,
      precio: parseFloat(document.getElementById('productoPrecio').value) || 0
    };
    try {
      if (id) { await API.productos.update(id, data); } else { await API.productos.create(data); }
      document.getElementById('productoModal').classList.remove('show');
      loadProductos();
    } catch (err) { alert(err.message); }
  });

  // ============================================================
  //  MOVIMIENTOS
  // ============================================================
  let productosCache = [];
  let bodegasCache = [];

  async function loadMovimientos() {
    try {
      [productosCache, bodegasCache] = await Promise.all([API.productos.list(), API.bodegas.list()]);
      const list = await API.movimientos.list();
      const tbody = document.getElementById('movimientosBody');
      if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No hay movimientos registrados</td></tr>';
        return;
      }
      const tipoTag = t => {
        const cls = t === 'ENTRADA' ? 'tag-entrada' : t === 'SALIDA' ? 'tag-salida' : 'tag-transfer';
        return '<span class="tag ' + cls + '">' + t + '</span>';
      };
      tbody.innerHTML = list.map(m => `
        <tr>
          <td>${m.id}</td>
          <td>${new Date(m.fecha).toLocaleString('es-EC')}</td>
          <td>${tipoTag(m.tipo)}</td>
          <td>${m.bodegaOrigenNombre || '—'} → ${m.bodegaDestinoNombre || '—'}</td>
          <td>${m.usuarioUsername || ''}</td>
          <td>${(m.detalles||[]).map(d => d.productoNombre + ' x' + d.cantidad).join(', ')}</td>
        </tr>`).join('');
    } catch (err) { showError('movimientos', err.message); }
  }

  document.getElementById('btnNewMovimiento').addEventListener('click', () => {
    openMovimientoModal();
  });

  function openMovimientoModal() {
    document.getElementById('movTipo').value = 'ENTRADA';
    populateBodegaSelects();
    rebuildDetalleRows();
    toggleBodegaFields();
    document.getElementById('movimientoModal').classList.add('show');
  }

  function populateBodegaSelects() {
    const opts = bodegasCache.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
    document.getElementById('movBodegaOrigen').innerHTML = opts;
    document.getElementById('movBodegaDestino').innerHTML = opts;
  }

  function toggleBodegaFields() {
    const tipo = document.getElementById('movTipo').value;
    document.getElementById('fieldOrigen').style.display = (tipo === 'ENTRADA') ? 'none' : '';
    document.getElementById('fieldDestino').style.display = (tipo === 'SALIDA') ? 'none' : '';
  }

  document.getElementById('movTipo').addEventListener('change', toggleBodegaFields);

  function rebuildDetalleRows() {
    const container = document.getElementById('detalleContainer');
    container.innerHTML = '';
    addDetalleRow();
  }

  function addDetalleRow() {
    const opts = productosCache.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
    const row = document.createElement('div');
    row.className = 'detalle-row';
    row.innerHTML = `
      <select class="det-producto">${opts}</select>
      <input type="number" class="det-cantidad" value="1" min="1" placeholder="Cant.">
      <button type="button" class="btn btn-danger btn-sm" onclick="this.parentElement.remove()">✕</button>
    `;
    document.getElementById('detalleContainer').appendChild(row);
  }

  document.getElementById('btnAddDetalle').addEventListener('click', addDetalleRow);

  document.getElementById('movimientoForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const tipo = document.getElementById('movTipo').value;
    const data = {
      tipo: tipo,
      bodegaOrigenId: tipo !== 'ENTRADA' ? parseInt(document.getElementById('movBodegaOrigen').value) : null,
      bodegaDestinoId: tipo !== 'SALIDA' ? parseInt(document.getElementById('movBodegaDestino').value) : null,
      detalles: []
    };
    document.querySelectorAll('.detalle-row').forEach(row => {
      data.detalles.push({
        productoId: parseInt(row.querySelector('.det-producto').value),
        cantidad: parseInt(row.querySelector('.det-cantidad').value)
      });
    });
    if (data.detalles.length === 0) { alert('Agregue al menos un detalle'); return; }
    try {
      await API.movimientos.create(data);
      document.getElementById('movimientoModal').classList.remove('show');
      loadMovimientos();
    } catch (err) { alert(err.message); }
  });

  // ============================================================
  //  REPORTES
  // ============================================================
  async function loadReportes() {
    try {
      const resumen = await API.reportes.resumen();

      document.getElementById('statBodegas').textContent = resumen.totalBodegas;
      document.getElementById('statProductos').textContent = resumen.totalProductos;
      document.getElementById('statStockTotal').textContent = resumen.stockTotalGlobal;

      // Stock por bodega
      document.getElementById('stockBodegaBody').innerHTML =
        resumen.stockTotalPorBodega.map(s => `
          <tr>
            <td>${s.bodegaNombre}</td>
            <td>${s.productoNombre}</td>
            <td>${s.cantidad}</td>
          </tr>`).join('');

      // Productos más movidos
      document.getElementById('prodMovidoBody').innerHTML =
        resumen.productosMasMovidos.map(p => `
          <tr>
            <td>${p.productoNombre}</td>
            <td>${p.totalMovido}</td>
          </tr>`).join('');

      // Stock bajo
      const bajos = await API.reportes.stockBajo();
      document.getElementById('stockBajoBody').innerHTML =
        bajos.length === 0
          ? '<tr><td colspan="3" class="empty-state">No hay productos con stock bajo</td></tr>'
          : bajos.map(p => `
            <tr>
              <td>${p.nombre}</td>
              <td>${p.categoria || ''}</td>
              <td><span class="badge-stock-bajo tag">${p.stock}</span></td>
            </tr>`).join('');
    } catch (err) { showError('reportes', err.message); }
  }

  // ============================================================
  //  AUDITORIAS (solo ADMIN)
  // ============================================================
  async function loadAuditorias() {
    try {
      const list = await API.auditorias.list();
      const tbody = document.getElementById('auditoriasBody');
      if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No hay registros de auditoría</td></tr>';
        return;
      }
      const tipoTag = t => '<span class="tag ' + (t === 'INSERT' ? 'tag-entrada' : t === 'DELETE' ? 'tag-salida' : 'tag-transfer') + '">' + t + '</span>';
      tbody.innerHTML = list.map(a => `
        <tr>
          <td>${a.id}</td>
          <td>${new Date(a.fechaHora).toLocaleString('es-EC')}</td>
          <td>${tipoTag(a.tipoOperacion)}</td>
          <td>${a.entidad}${a.entidadId ? ' #' + a.entidadId : ''}</td>
          <td>${a.usuario || ''}</td>
          <td style="max-width:300px;font-size:11px;color:var(--text-light);">
            ${a.valoresAnteriores ? '<b>Ant:</b> ' + truncate(a.valoresAnteriores, 120) : ''}
            ${a.valoresNuevos ? '<br><b>Nuevo:</b> ' + truncate(a.valoresNuevos, 120) : ''}
          </td>
        </tr>`).join('');
    } catch (err) { showError('auditorias', err.message); }
  }

  // Filtros de auditoría
  document.getElementById('btnFiltroUsuario').addEventListener('click', async () => {
    const u = document.getElementById('filtroUsuario').value.trim();
    if (!u) return;
    try {
      const list = await API.auditorias.byUsuario(u);
      renderAuditorias(list);
    } catch (err) { alert(err.message); }
  });

  document.getElementById('btnFiltroTipo').addEventListener('click', async () => {
    const t = document.getElementById('filtroTipo').value;
    try {
      const list = await API.auditorias.byTipo(t);
      renderAuditorias(list);
    } catch (err) { alert(err.message); }
  });

  document.getElementById('btnFiltroEntidad').addEventListener('click', async () => {
    const e = document.getElementById('filtroEntidad').value.trim();
    if (!e) return;
    try {
      const list = await API.auditorias.byEntidad(e);
      renderAuditorias(list);
    } catch (err) { alert(err.message); }
  });

  document.getElementById('btnAudAll').addEventListener('click', loadAuditorias);

  function renderAuditorias(list) {
    const tipoTag = t => '<span class="tag ' + (t === 'INSERT' ? 'tag-entrada' : t === 'DELETE' ? 'tag-salida' : 'tag-transfer') + '">' + t + '</span>';
    document.getElementById('auditoriasBody').innerHTML = list.map(a => `
      <tr>
        <td>${a.id}</td>
        <td>${new Date(a.fechaHora).toLocaleString('es-EC')}</td>
        <td>${tipoTag(a.tipoOperacion)}</td>
        <td>${a.entidad}${a.entidadId ? ' #' + a.entidadId : ''}</td>
        <td>${a.usuario || ''}</td>
        <td style="max-width:300px;font-size:11px;color:var(--text-light);">
          ${a.valoresAnteriores ? '<b>Ant:</b> ' + truncate(a.valoresAnteriores, 120) : ''}
          ${a.valoresNuevos ? '<br><b>Nuevo:</b> ' + truncate(a.valoresNuevos, 120) : ''}
        </td>
      </tr>`).join('');
  }

  // ============================================================
  //  Helpers
  // ============================================================
  function adminActions(tipo, id) {
    if (!isAdmin) return '';
    return `
      <button class="btn btn-sm btn-primary" data-edit="${tipo}" data-id="${id}">Editar</button>
      <button class="btn btn-sm btn-danger" data-del="${tipo}" data-id="${id}">Eliminar</button>`;
  }

  function bindEditDelete(tipo) {
    document.querySelectorAll('[data-edit="' + tipo + '"]').forEach(btn => {
      btn.addEventListener('click', async () => {
        const id = btn.dataset.id;
        if (tipo === 'bodega') {
          const b = await API.bodegas.get(id);
          openBodegaModal(id, b);
        } else {
          const p = await API.productos.get(id);
          openProductoModal(id, p);
        }
      });
    });
    document.querySelectorAll('[data-del="' + tipo + '"]').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (!confirm('¿Eliminar el registro #' + btn.dataset.id + '?')) return;
        try {
          if (tipo === 'bodega') { await API.bodegas.remove(btn.dataset.id); loadBodegas(); }
          else { await API.productos.remove(btn.dataset.id); loadProductos(); }
        } catch (err) { alert(err.message); }
      });
    });
  }

  function truncate(s, n) {
    if (!s) return '';
    return s.length > n ? s.substring(0, n) + '…' : s;
  }

  function showError(tab, msg) {
    const section = document.getElementById(tab);
    let err = section.querySelector('.empty-state');
    if (!err) {
      err = document.createElement('div');
      err.className = 'empty-state';
      section.querySelector('.table-wrapper')?.prepend(err) || section.prepend(err);
    }
    err.textContent = 'Error: ' + msg;
  }

  // Cerrar modales
  document.querySelectorAll('[data-close]').forEach(btn => {
    btn.addEventListener('click', () => {
      btn.closest('.modal-overlay').classList.remove('show');
    });
  });

  // Control de pestañas basado en rol
  if (!isAdmin) {
    document.getElementById('btnNewBodega').style.display = 'none';
    document.getElementById('btnNewProducto').style.display = 'none';
  }

  function loadTab(tab) {
    switch (tab) {
      case 'bodegas': loadBodegas(); break;
      case 'productos': loadProductos(); break;
      case 'movimientos': loadMovimientos(); break;
      case 'reportes': loadReportes(); break;
      case 'auditorias': loadAuditorias(); break;
    }
  }
});
