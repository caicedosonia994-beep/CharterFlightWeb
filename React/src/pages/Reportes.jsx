/**
 * Reportes - Página de reportes y análisis.
 *
 * Dashboard con estadísticas operativas y financieras.
 * Gráfico de ingresos construido con SVG puro.
 * Preparado para conectar con el backend cuando exista el endpoint.
 */
import { useState, useEffect } from 'react'

export default function Reportes() {
  const [stats] = useState([
    { label: 'Clientes Registrados', value: '1,248', change: '+12%', icon: 'fa-users', positive: true },
    { label: 'Vuelos Disponibles', value: '32', change: '8 Activos', icon: 'fa-plane', positive: true },
    { label: 'Reservas Activas', value: '86', change: '94% Ocup.', icon: 'fa-calendar-check', positive: true },
    { label: 'Ingresos Totales', value: '$2.4M', change: '+18.5%', icon: 'fa-sack-dollar', positive: true },
  ])

  const [chartData] = useState({
    labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
    vuelos: [120, 150, 140, 190, 240, 210],
    proyeccion: [80, 110, 100, 150, 200, 180],
  })

  const maxValor = Math.max(...chartData.vuelos, ...chartData.proyeccion)
  const anchoGrafico = 800
  const altoGrafico = 300
  const padding = { top: 20, right: 20, bottom: 40, left: 50 }
  const anchoUtil = anchoGrafico - padding.left - padding.right
  const altoUtil = altoGrafico - padding.top - padding.bottom

  const puntosVuelos = chartData.vuelos.map((valor, indice) => {
    const x = padding.left + (indice / (chartData.labels.length - 1)) * anchoUtil
    const y = padding.top + altoUtil - (valor / maxValor) * altoUtil
    return { x, y }
  })

  const puntosProyeccion = chartData.proyeccion.map((valor, indice) => {
    const x = padding.left + (indice / (chartData.labels.length - 1)) * anchoUtil
    const y = padding.top + altoUtil - (valor / maxValor) * altoUtil
    return { x, y }
  })

  const generarPath = (puntos) => {
    return puntos.map((punto, i) => `${i === 0 ? 'M' : 'L'} ${punto.x} ${punto.y}`).join(' ')
  }

  const generarArea = (puntos) => {
    const path = generarPath(puntos)
    const ultimo = puntos[puntos.length - 1]
    const primero = puntos[0]
    return `${path} L ${ultimo.x} ${altoGrafico - padding.bottom} L ${primero.x} ${altoGrafico - padding.bottom} Z`
  }

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 p-6 lg:p-12">
        <div className="max-w-7xl mx-auto">
          <header className="mb-12 flex justify-between items-end">
            <div>
              <h1 className="text-3xl font-serif text-white">Panel de Control Ejecutivo</h1>
              <p className="text-gray-400 text-sm mt-1">Visión general del rendimiento operativo y financiero.</p>
            </div>
            <div className="text-right">
              <p className="text-xs font-bold uppercase tracking-widest text-dorado mb-1">Mayo 2026</p>
              <p className="text-gray-500 text-xs">Actualizado hace 5 min</p>
            </div>
          </header>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
            {stats.map((stat, index) => (
              <div key={index} className="stat-card p-6 rounded-2xl border border-white/5">
                <div className="flex justify-between items-start mb-4">
                  <div className="p-2 bg-vinotinto/30 rounded-lg border border-vinotinto/50">
                    <i className={`fa-solid ${stat.icon} text-dorado`}></i>
                  </div>
                  <span className={`text-xs font-bold ${stat.positive ? 'text-green-500' : 'text-red-400'}`}>{stat.change}</span>
                </div>
                <p className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">{stat.label}</p>
                <h3 className="text-3xl font-serif">{stat.value}</h3>
              </div>
            ))}
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 bg-surface p-8 rounded-2xl border border-white/5">
              <h4 className="text-lg font-serif mb-6 text-dorado">Evolución de Ingresos</h4>
              <div className="overflow-x-auto">
                <svg viewBox={`0 0 ${anchoGrafico} ${altoGrafico}`} className="w-full min-w-[600px]">
                  <rect x={padding.left} y={padding.top} width={anchoUtil} height={altoUtil} fill="none" />
                  {[0, 0.25, 0.5, 0.75, 1].map((tick, i) => {
                    const y = padding.top + altoUtil - tick * altoUtil
                    const valor = Math.round(tick * maxValor)
                    return (
                      <g key={i}>
                        <line x1={padding.left} y1={y} x2={anchoGrafico - padding.right} y2={y} stroke="rgba(255,255,255,0.05)" />
                        <text x={padding.left - 10} y={y + 4} textAnchor="end" fill="#6b7280" fontSize="10">{valor}</text>
                      </g>
                    )
                  })}
                  {chartData.labels.map((label, i) => {
                    const x = padding.left + (i / (chartData.labels.length - 1)) * anchoUtil
                    return (
                      <text key={i} x={x} y={altoGrafico - 10} textAnchor="middle" fill="#6b7280" fontSize="10">
                        {label}
                      </text>
                    )
                  })}
                  <path d={generarArea(puntosProyeccion)} fill="rgba(90, 15, 36, 0.4)" />
                  <path d={generarPath(puntosProyeccion)} fill="none" stroke="#5A0F24" strokeWidth="2" />
                  <path d={generarArea(puntosVuelos)} fill="rgba(212, 175, 55, 0.05)" />
                  <path d={generarPath(puntosVuelos)} fill="none" stroke="#D4AF37" strokeWidth="3" />
                  {puntosVuelos.map((punto, i) => (
                    <circle key={i} cx={punto.x} cy={punto.y} r="4" fill="#D4AF37" />
                  ))}
                </svg>
              </div>
              <div className="flex items-center justify-center space-x-6 mt-4 text-xs text-gray-500">
                <div className="flex items-center space-x-2">
                  <span className="w-3 h-3 rounded-full bg-dorado"></span>
                  <span>Vuelos Realizados</span>
                </div>
                <div className="flex items-center space-x-2">
                  <span className="w-3 h-3 rounded-full bg-vinotinto"></span>
                  <span>Proyección</span>
                </div>
              </div>
            </div>

            <div className="bg-surface p-8 rounded-2xl border border-white/5">
              <h4 className="text-lg font-serif mb-6 text-dorado">Actividad Reciente</h4>
              <div className="space-y-6">
                <div className="flex items-center space-x-4">
                  <div className="w-2 h-2 rounded-full bg-green-500"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium">Nueva Reserva Confirmada</p>
                    <p className="text-xs text-gray-500">Alejandro Vega - Vuelo CH-742</p>
                  </div>
                  <span className="text-[10px] text-gray-600">2 min</span>
                </div>
                <div className="flex items-center space-x-4">
                  <div className="w-2 h-2 rounded-full bg-dorado"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium">Vuelo Programado</p>
                    <p className="text-xs text-gray-500">Ruta MAD -&gt; IBZ registrada</p>
                  </div>
                  <span className="text-[10px] text-gray-600">45 min</span>
                </div>
                <div className="flex items-center space-x-4">
                  <div className="w-2 h-2 rounded-full bg-vinotinto"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium">Pago Recibido</p>
                    <p className="text-xs text-gray-500">Ref: INV-88229 - $12,500</p>
                  </div>
                  <span className="text-[10px] text-gray-600">2h</span>
                </div>
                <div className="flex items-center space-x-4">
                  <div className="w-2 h-2 rounded-full bg-gray-600"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium">Cliente Actualizado</p>
                    <p className="text-xs text-gray-500">Preferencias de catering VR</p>
                  </div>
                  <span className="text-[10px] text-gray-600">5h</span>
                </div>
              </div>
              <button onClick={() => alert('Funcionalidad disponible cuando el backend implemente el endpoint de reportes.')} className="w-full mt-8 py-3 border border-white/10 rounded-lg text-xs font-bold uppercase tracking-widest hover:bg-white/5 transition-colors">
                Ver todo el historial
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}
