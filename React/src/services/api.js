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
