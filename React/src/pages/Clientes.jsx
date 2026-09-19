/**
 * Clientes - Página de listado de clientes.
 *
 * Obtiene la lista de clientes desde el backend Java mediante fetch.
 * Permite buscar, editar y eliminar clientes.
 * Utiliza useState para el estado local y useEffect para la carga inicial.
 */
import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getClientes, deleteCliente } from '../services/api'

export default function Clientes() {
  const [clientes, setClientes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchClientes()
  }, [])

  const fetchClientes = async () => {
    try {
      setLoading(true)
      const data = await getClientes()
      setClientes(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('¿Está seguro de eliminar este cliente?')) return
    try {
      await deleteCliente(id)
      setClientes(clientes.filter(c => c.idCliente !== id))
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 p-6 lg:p-12">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
            <div>
              <h1 className="text-3xl font-serif text-white">Base de Datos de Clientes</h1>
              <p className="text-gray-400 text-sm mt-1">Gestión de perfiles VIP y registros históricos.</p>
            </div>
            <div className="flex flex-col sm:flex-row w-full md:w-auto gap-4">
              <form onSubmit={(e) => { e.preventDefault(); /* TODO: implementar búsqueda */ }} className="relative group">
                <i className="fa-solid fa-magnifying-glass absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 group-focus-within:text-dorado transition-colors"></i>
                <input
                  type="text"
                  placeholder="Buscar cliente..."
                  className="bg-surface border border-white/10 rounded-full pl-11 pr-6 py-2.5 text-sm w-full sm:w-64 focus:outline-none focus:border-dorado transition-all"
                />
              </form>
              <Link to="/clientes/nuevo" className="bg-dorado text-darkbg px-6 py-2.5 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-white transition-colors text-center">
                Nuevo Cliente
              </Link>
            </div>
          </div>

          {error && (
            <div className="mb-6 p-4 rounded-lg border border-red-500/30 bg-red-500/10 text-red-400">
              {error}
            </div>
          )}

          <div className="bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-vinotinto/20 border-b border-white/5">
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">ID</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Nombre Completo</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Documento</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Teléfono</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Email</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/5">
                  {loading && (
                    <tr>
                      <td colSpan="6" className="px-6 py-8 text-center text-gray-500">Cargando...</td>
                    </tr>
                  )}
                  {!loading && clientes.length === 0 && (
                    <tr>
                      <td colSpan="6" className="px-6 py-8 text-center text-gray-500">No hay clientes registrados.</td>
                    </tr>
                  )}
                  {!loading && clientes.map((cliente) => (
                    <tr key={cliente.idCliente} className="hover:bg-white/[0.02] transition-colors">
                      <td className="px-6 py-4 text-sm font-mono text-gray-500">CF-{cliente.idCliente}</td>
                      <td className="px-6 py-4">
                        <div className="flex items-center space-x-3">
                          <div className="w-8 h-8 rounded-full bg-vinotinto/40 flex items-center justify-center text-[10px] font-bold border border-dorado/20">
                            {cliente.nombre[0]}{cliente.apellido[0]}
                          </div>
                          <span className="text-sm font-medium">{cliente.nombre} {cliente.apellido}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-400">{cliente.documento}</td>
                      <td className="px-6 py-4 text-sm text-gray-400">{cliente.telefono}</td>
                      <td className="px-6 py-4 text-sm text-gray-400">{cliente.correo}</td>
                      <td className="px-6 py-4 text-right space-x-2">
                        <Link to={`/clientes/editar/${cliente.idCliente}`} className="p-2 text-gray-500 hover:text-dorado transition-colors" title="Editar">
                          <i className="fa-solid fa-pen-to-square"></i>
                        </Link>
                        <button onClick={() => handleDelete(cliente.idCliente)} className="p-2 text-gray-500 hover:text-red-500 transition-colors" title="Eliminar">
                          <i className="fa-solid fa-trash-can"></i>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}
