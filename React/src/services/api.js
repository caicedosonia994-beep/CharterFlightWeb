const API_BASE = '/api'

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
    throw new Error(err.message || 'Error al crear cliente')
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
    throw new Error(err.message || 'Error al actualizar cliente')
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
