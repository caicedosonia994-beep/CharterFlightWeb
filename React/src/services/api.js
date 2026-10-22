const API_BASE = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')

export async function getClientes() {
  const response = await fetch(`${API_BASE}/clientes`)
  if (!response.ok) throw new Error('Error al obtener clientes')
  return response.json()
}

export async function getCliente(id) {
  const response = await fetch(`${API_BASE}/clientes/${id}`)
  if (!response.ok) throw new Error('Cliente no encontrado')
  return response.json()
}

export async function createCliente(cliente) {
  const response = await fetch(`${API_BASE}/clientes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cliente),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || err.message || 'Error al crear cliente')
  }
  return response.json()
}

export async function updateCliente(id, cliente) {
  const response = await fetch(`${API_BASE}/clientes/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cliente),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || err.message || 'Error al actualizar cliente')
  }
  return response.json()
}

export async function deleteCliente(id) {
  const response = await fetch(`${API_BASE}/clientes/${id}`, { method: 'DELETE' })
  if (!response.ok) throw new Error('Error al eliminar cliente')
  return response.json()
}

export async function getVuelos() {
  const response = await fetch(`${API_BASE}/vuelos`)
  if (!response.ok) throw new Error('Error al obtener vuelos')
  return response.json()
}

export async function getVuelo(id) {
  const response = await fetch(`${API_BASE}/vuelos/${id}`)
  if (!response.ok) throw new Error('Vuelo no encontrado')
  return response.json()
}

export async function createVuelo(vuelo) {
  const response = await fetch(`${API_BASE}/vuelos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(vuelo),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || 'Error al crear vuelo')
  }
  return response.json()
}

export async function updateVuelo(id, vuelo) {
  const response = await fetch(`${API_BASE}/vuelos/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(vuelo),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || 'Error al actualizar vuelo')
  }
  return response.json()
}

export async function deleteVuelo(id) {
  const response = await fetch(`${API_BASE}/vuelos/${id}`, { method: 'DELETE' })
  if (!response.ok) throw new Error('Error al eliminar vuelo')
  return response.json()
}

export async function getReservas() {
  const response = await fetch(`${API_BASE}/reservas`)
  if (!response.ok) throw new Error('Error al obtener reservas')
  return response.json()
}

export async function getReserva(id) {
  const response = await fetch(`${API_BASE}/reservas/${id}`)
  if (!response.ok) throw new Error('Reserva no encontrada')
  return response.json()
}

export async function createReserva(reserva) {
  const response = await fetch(`${API_BASE}/reservas`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reserva),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || 'Error al crear reserva')
  }
  return response.json()
}

export async function updateReserva(id, reserva) {
  const response = await fetch(`${API_BASE}/reservas/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reserva),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || 'Error al actualizar reserva')
  }
  return response.json()
}

export async function deleteReserva(id) {
  const response = await fetch(`${API_BASE}/reservas/${id}`, { method: 'DELETE' })
  if (!response.ok) throw new Error('Error al eliminar reserva')
  return response.json()
}

export async function getPagos() {
  const response = await fetch(`${API_BASE}/pagos`)
  if (!response.ok) throw new Error('Error al obtener pagos')
  return response.json()
}

export async function getPago(id) {
  const response = await fetch(`${API_BASE}/pagos/${id}`)
  if (!response.ok) throw new Error('Pago no encontrado')
  return response.json()
}

export async function createPago(pago) {
  const response = await fetch(`${API_BASE}/pagos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(pago),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || 'Error al crear pago')
  }
  return response.json()
}

export async function updatePago(id, pago) {
  const response = await fetch(`${API_BASE}/pagos/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(pago),
  })
  if (!response.ok) {
    const err = await response.json()
    throw new Error(err.error || 'Error al actualizar pago')
  }
  return response.json()
}

export async function deletePago(id) {
  const response = await fetch(`${API_BASE}/pagos/${id}`, { method: 'DELETE' })
  if (!response.ok) throw new Error('Error al eliminar pago')
  return response.json()
}


async function leerRespuestaSolicitud(response, mensajePredeterminado) {
  const texto = await response.text()
  let resultado = null
  if (texto) {
    try {
      resultado = JSON.parse(texto)
    } catch {
      resultado = null
    }
  }
  if (!response.ok) {
    throw new Error(resultado?.error || resultado?.message || mensajePredeterminado)
  }
  return resultado
}

export async function getSolicitudesEspeciales() {
  const response = await fetch(`${API_BASE}/solicitudes-especiales`)
  return leerRespuestaSolicitud(response, 'Error al obtener solicitudes especiales')
}

export async function getSolicitudEspecial(id) {
  const response = await fetch(`${API_BASE}/solicitudes-especiales/${id}`)
  return leerRespuestaSolicitud(response, 'Solicitud no encontrada')
}

export async function createSolicitudEspecial(data) {
  const response = await fetch(`${API_BASE}/solicitudes-especiales`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  return leerRespuestaSolicitud(response, 'Error al crear solicitud especial')
}

export async function updateSolicitudEspecial(id, data) {
  const response = await fetch(`${API_BASE}/solicitudes-especiales/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  return leerRespuestaSolicitud(response, 'Error al actualizar solicitud especial')
}

async function leerRespuestaApi(response, mensajePredeterminado) {
  const texto = await response.text()
  let resultado = null
  if (texto) {
    try { resultado = JSON.parse(texto) } catch { resultado = null }
  }
  if (!response.ok) {
    throw new Error(resultado?.error || resultado?.message || mensajePredeterminado)
  }
  return resultado
}

export async function getOperacionesCobrables() {
  const response = await fetch(`${API_BASE}/operaciones-cobrables`)
  return leerRespuestaApi(response, 'Error al obtener las operaciones de pago')
}

export async function getOperacionCobrable(id) {
  const response = await fetch(`${API_BASE}/operaciones-cobrables/${id}`)
  return leerRespuestaApi(response, 'Operación de pago no encontrada')
}

export async function createOperacionCobrable(operacion) {
  const response = await fetch(`${API_BASE}/operaciones-cobrables`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(operacion),
  })
  return leerRespuestaApi(response, 'Error al crear la operación de pago')
}

export async function updateOperacionCobrable(id, datos) {
  const response = await fetch(`${API_BASE}/operaciones-cobrables/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos),
  })
  return leerRespuestaApi(response, 'Error al actualizar la operación de pago')
}

export async function createMovimientoPago(id, movimiento) {
  const response = await fetch(`${API_BASE}/operaciones-cobrables/${id}/movimientos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(movimiento),
  })
  return leerRespuestaApi(response, 'Error al registrar el movimiento de pago')
}

export async function getReporte(tipo, filtros = {}) {
  const parametros = new URLSearchParams()
  Object.entries(filtros).forEach(([clave, valor]) => {
    if (valor !== undefined && valor !== null && valor !== '') parametros.set(clave, valor)
  })
  const query = parametros.toString()
  const response = await fetch(`${API_BASE}/reportes/${tipo}${query ? `?${query}` : ''}`)
  return leerRespuestaApi(response, 'Error al consultar el reporte')
}

export async function exportarReporte(tipo, formato, filtros = {}) {
  const parametros = new URLSearchParams()
  Object.entries(filtros).forEach(([clave, valor]) => {
    if (valor !== undefined && valor !== null && valor !== '') parametros.set(clave, valor)
  })
  const query = parametros.toString()
  const response = await fetch(`${API_BASE}/reportes/${tipo}/${formato}${query ? `?${query}` : ''}`)
  if (!response.ok) return leerRespuestaApi(response, `Error al generar ${formato.toUpperCase()}`)
  return response.blob()
}
