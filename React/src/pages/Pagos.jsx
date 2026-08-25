/**
 * Pagos - Página de facturación y pagos.
 *
 * Módulo financiero para gestión de pagos y facturación.
 * Preparado para conectar con el backend cuando exista el endpoint.
 */
import { useState } from 'react'

export default function Pagos() {
  const [pagos] = useState([
    { id: 'INV-001', cliente: 'Alejandro Vega', vuelo: 'CH-742', monto: '$12,500', estado: 'Pagado', fecha: '2026-05-20' },
    { id: 'INV-002', cliente: 'Valentina Ríos', vuelo: 'CH-910', monto: '$8,200', estado: 'Pendiente', fecha: '2026-05-22' },
    { id: 'INV-003', cliente: 'Roberto Castillo', vuelo: 'CH-115', monto: '$15,800', estado: 'Pagado', fecha: '2026-05-24' },
  ])

  const getEstadoColor = (estado) => {
    switch (estado) {
      case 'Pagado':
        return 'text-green-400 bg-green-500/10 border-green-500/30'
      case 'Pendiente':
        return 'text-dorado bg-dorado/10 border-dorado/30'
      case 'Cancelado':
        return 'text-red-400 bg-red-500/10 border-red-500/30'
      default:
        return 'text-gray-400 bg-gray-500/10 border-gray-500/30'
    }
  }

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 p-6 lg:p-12">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
            <div>
              <h1 className="text-3xl font-serif text-white">Facturación y Pagos</h1>
              <p className="text-gray-400 text-sm mt-1">Control financiero, facturación automática y auditoría de pagos.</p>
            </div>
            <button onClick={() => alert('Funcionalidad disponible cuando el backend implemente el endpoint de pagos.')} className="bg-dorado text-darkbg px-6 py-2.5 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-white transition-colors">
              Nueva Factura
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
            <div className="stat-card p-6 rounded-2xl border border-white/5">
              <p className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">Ingresos del Mes</p>
              <h3 className="text-3xl font-serif text-dorado">$36,500</h3>
            </div>
            <div className="stat-card p-6 rounded-2xl border border-white/5">
              <p className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">Pagos Pendientes</p>
              <h3 className="text-3xl font-serif">2</h3>
            </div>
            <div className="stat-card p-6 rounded-2xl border border-white/5">
              <p className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">Facturas Emitidas</p>
              <h3 className="text-3xl font-serif">3</h3>
            </div>
          </div>

          <div className="bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-vinotinto/20 border-b border-white/5">
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Factura</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Cliente</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Vuelo</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Monto</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Estado</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Fecha</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/5">
                  {pagos.map((pago) => (
                    <tr key={pago.id} className="hover:bg-white/[0.02] transition-colors">
                      <td className="px-6 py-4 text-sm font-mono text-gray-500">{pago.id}</td>
                      <td className="px-6 py-4 text-sm font-medium">{pago.cliente}</td>
                      <td className="px-6 py-4 text-sm text-gray-400">{pago.vuelo}</td>
                      <td className="px-6 py-4 text-sm text-dorado font-bold">{pago.monto}</td>
                      <td className="px-6 py-4">
                        <span className={`text-xs font-bold uppercase tracking-widest px-3 py-1 rounded-full border ${getEstadoColor(pago.estado)}`}>
                          {pago.estado}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-400">{pago.fecha}</td>
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
