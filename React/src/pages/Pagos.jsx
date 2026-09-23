import { useState, useEffect } from 'react'
import {
  getPagos,
  getReservas,
  getClientes,
  getVuelos,
  createPago,
} from '../services/api'

export default function Pagos() {
  const [pagos, setPagos] = useState([])
  const [reservas, setReservas] = useState([])
  const [clientes, setClientes] = useState([])
  const [vuelos, setVuelos] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [formData, setFormData] = useState({
    idReserva: '',
    idCliente: '',
    idVuelo: '',
    monto: '',
    numeroFactura: '',
    fechaPago: '',
    estado: 'Pagado',
  })

  useEffect(() => {
    fetchInitialData()
  }, [])

  const fetchInitialData = async () => {
    try {
      setLoading(true)
      setError('')

      const resultados = await Promise.all([
        getPagos(),
        getReservas(),
        getClientes(),
        getVuelos(),
      ])

      setPagos(resultados[0])
      setReservas(resultados[1])
      setClientes(resultados[2])
      setVuelos(resultados[3])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    const name = e.target.name
    const value = e.target.value

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleReservaChange = (e) => {
    const selectedId = e.target.value

    if (!selectedId) {
      setFormData((prev) => ({
        ...prev,
        idReserva: '',
        idCliente: '',
        idVuelo: '',
      }))
      return
    }

    const reserva = reservas.find(
      (r) => Number(r.idReserva) === Number(selectedId)
    )

    if (reserva) {
      setFormData((prev) => ({
        ...prev,
        idReserva: selectedId,
        idCliente: String(reserva.idCliente),
        idVuelo: String(reserva.idVuelo),
      }))
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    try {
      if (!formData.idReserva) {
        setError('Debe seleccionar una reserva.')
        return
      }

      if (!formData.idCliente) {
        setError('Debe seleccionar un cliente.')
        return
      }

      if (!formData.idVuelo) {
        setError('Debe seleccionar un vuelo.')
        return
      }

      const payload = {
        idReserva: Number(formData.idReserva),
        idCliente: Number(formData.idCliente),
        idVuelo: Number(formData.idVuelo),
        monto: Number(formData.monto),
        numeroFactura: formData.numeroFactura,
        fechaPago: formData.fechaPago,
        estado: formData.estado,
      }

      await createPago(payload)

      setFormData({
        idReserva: '',
        idCliente: '',
        idVuelo: '',
        monto: '',
        numeroFactura: '',
        fechaPago: '',
        estado: 'Pagado',
      })

      await fetchInitialData()
    } catch (err) {
      setError(err.message)
    }
  }

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

  const formatClienteLabel = (cliente) => {
    if (cliente.nombre && cliente.apellido) {
      return cliente.nombre + ' ' + cliente.apellido
    }

    if (cliente.nombre) {
      return cliente.nombre
    }

    if (cliente.apellido) {
      return cliente.apellido
    }

    return 'Cliente #' + cliente.idCliente
  }

  const formatVueloLabel = (vuelo) => {
    if (vuelo.codigo && vuelo.origen && vuelo.destino) {
      return (
        vuelo.codigo +
        ' | ' +
        vuelo.origen +
        ' -> ' +
        vuelo.destino
      )
    }

    if (vuelo.codigo) {
      return vuelo.codigo
    }

    return 'Vuelo #' + vuelo.idVuelo
  }

  const formatReservaLabel = (reserva) => {
    const cliente = clientes.find(
      (c) => Number(c.idCliente) === Number(reserva.idCliente)
    )

    const vuelo = vuelos.find(
      (v) => Number(v.idVuelo) === Number(reserva.idVuelo)
    )

    const clienteNombre = cliente
      ? formatClienteLabel(cliente)
      : 'Cliente #' + reserva.idCliente

    const vueloNombre = vuelo
      ? formatVueloLabel(vuelo)
      : 'Vuelo #' + reserva.idVuelo

    return (
      'Reserva #' +
      reserva.idReserva +
      ' | Cliente: ' +
      clienteNombre +
      ' | Vuelo: ' +
      vueloNombre +
      ' | Pasajeros: ' +
      reserva.pasajeros
    )
  }

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 p-6 lg:p-12">
        <div className="max-w-7xl mx-auto">

          <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
            <div>
              <h1 className="text-3xl font-serif text-white">
                Facturación y Pagos
              </h1>

              <p className="text-gray-400 text-sm mt-1">
                Control financiero, facturación automática y auditoría de pagos.
              </p>
            </div>

            <button
              onClick={() =>
                document
                  .getElementById('nueva-factura-form')
                  .scrollIntoView({ behavior: 'smooth' })
              }
              className="bg-dorado text-darkbg px-6 py-2.5 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-white transition-colors"
            >
              Nueva Factura
            </button>
          </div>

          {error && (
            <div className="mb-6 p-4 rounded-lg border border-red-500/30 bg-red-500/10 text-red-400">
              {error}
            </div>
          )}

          <div className="bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">

            <div className="p-6 border-b border-white/5">

              <h2 className="text-xl font-serif text-dorado mb-4">
                Nueva Factura
              </h2>

              <form
                id="nueva-factura-form"
                onSubmit={handleSubmit}
                className="grid grid-cols-1 md:grid-cols-2 gap-6"
              >

                <div className="space-y-2">
                  <label
                    className="text-xs font-bold uppercase tracking-widest text-gray-400"
                    htmlFor="idReserva"
                  >
                    Reserva
                  </label>

                  <select
                    id="idReserva"
                    name="idReserva"
                    value={formData.idReserva}
                    onChange={handleReservaChange}
                    className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors"
                    required
                  >
                    <option value="">
                      Seleccionar reserva...
                    </option>

                    {reservas.map((reserva) => (
                      <option
                        key={reserva.idReserva}
                        value={reserva.idReserva}
                      >
                        {formatReservaLabel(reserva)}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="space-y-2">
                  <label
                    className="text-xs font-bold uppercase tracking-widest text-gray-400"
                    htmlFor="idCliente"
                  >
                    Cliente
                  </label>

                  <select
                    id="idCliente"
                    name="idCliente"
                    value={formData.idCliente}
                    onChange={handleChange}
                    className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors"
                    required
                  >
                    <option value="">
                      Seleccionar cliente...
                    </option>

                    {clientes.map((cliente) => (
                      <option
                        key={cliente.idCliente}
                        value={cliente.idCliente}
                      >
                        {formatClienteLabel(cliente)}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="space-y-2">
                  <label
                    className="text-xs font-bold uppercase tracking-widest text-gray-400"
                    htmlFor="idVuelo"
                  >
                    Vuelo
                  </label>

                  <select
                    id="idVuelo"
                    name="idVuelo"
                    value={formData.idVuelo}
                    onChange={handleChange}
                    className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors"
                    required
                  >
                    <option value="">
                      Seleccionar vuelo...
                    </option>

                    {vuelos.map((vuelo) => (
                      <option
                        key={vuelo.idVuelo}
                        value={vuelo.idVuelo}
                      >
                        {formatVueloLabel(vuelo)}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="space-y-2">
                  <label
                    className="text-xs font-bold uppercase tracking-widest text-gray-400"
                    htmlFor="monto"
                  >
                    Monto
                  </label>

                  <input
                    type="number"
                    step="0.01"
                    id="monto"
                    name="monto"
                    value={formData.monto}
                    onChange={handleChange}
                    placeholder="Ej. 12500.00"
                    className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <label
                    className="text-xs font-bold uppercase tracking-widest text-gray-400"
                    htmlFor="numeroFactura"
                  >
                    Número de Factura
                  </label>

                  <input
                    type="text"
                    id="numeroFactura"
                    name="numeroFactura"
                    value={formData.numeroFactura}
                    onChange={handleChange}
                    placeholder="Ej. FAC-001234"
                    className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <label
                    className="text-xs font-bold uppercase tracking-widest text-gray-400"
                    htmlFor="fechaPago"
                  >
                    Fecha de Pago
                  </label>

                  <input
                    type="date"
                    id="fechaPago"
                    name="fechaPago"
                    value={formData.fechaPago}
                    onChange={handleChange}
                    className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors text-gray-400"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <label
                    className="text-xs font-bold uppercase tracking-widest text-gray-400"
                    htmlFor="estado"
                  >
                    Estado
                  </label>

                  <select
                    id="estado"
                    name="estado"
                    value={formData.estado}
                    onChange={handleChange}
                    className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none"
                  >
                    <option value="Pagado">Pagado</option>
                    <option value="Pendiente">Pendiente</option>
                    <option value="Cancelado">Cancelado</option>
                  </select>
                </div>

                <div className="flex items-end">
                  <button
                    type="submit"
                    className="w-full flex-1 bg-dorado text-darkbg py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white transition-colors flex items-center justify-center space-x-2"
                  >
                    <i className="fa-solid fa-check"></i>
                    <span>Confirmar Factura</span>
                  </button>
                </div>

              </form>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 p-6 border-b border-white/5 mb-0">

              <div className="stat-card p-6 rounded-2xl border border-white/5">
                <p className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">
                  Ingresos del Mes
                </p>

                <h3 className="text-3xl font-serif text-dorado">
                  $
                  {pagos
                    .filter((p) => p.estado === 'Pagado')
                    .reduce(
                      (sum, p) => sum + Number(p.monto),
                      0
                    )
                    .toLocaleString()}
                </h3>
              </div>

              <div className="stat-card p-6 rounded-2xl border border-white/5">
                <p className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">
                  Pagos Pendientes
                </p>

                <h3 className="text-3xl font-serif">
                  {
                    pagos.filter(
                      (p) => p.estado === 'Pendiente'
                    ).length
                  }
                </h3>
              </div>

              <div className="stat-card p-6 rounded-2xl border border-white/5">
                <p className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">
                  Facturas Emitidas
                </p>

                <h3 className="text-3xl font-serif">
                  {pagos.length}
                </h3>
              </div>

            </div>

            <div className="overflow-x-auto">

              <table className="w-full text-left border-collapse">

                <thead>
                  <tr className="bg-vinotinto/20 border-b border-white/5">

                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">
                      Factura
                    </th>

                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">
                      Cliente
                    </th>

                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">
                      Vuelo
                    </th>

                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">
                      Monto
                    </th>

                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">
                      Estado
                    </th>

                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">
                      Fecha
                    </th>

                  </tr>
                </thead>

                <tbody className="divide-y divide-white/5">

                  {loading && (
                    <tr>
                      <td
                        colSpan="6"
                        className="px-6 py-8 text-center text-gray-500"
                      >
                        Cargando...
                      </td>
                    </tr>
                  )}

                  {!loading && pagos.length === 0 && (
                    <tr>
                      <td
                        colSpan="6"
                        className="px-6 py-8 text-center text-gray-500"
                      >
                        No hay pagos registrados.
                      </td>
                    </tr>
                  )}

                  {!loading &&
                    pagos.map((pago) => {

                      const cliente = clientes.find(
                        (c) =>
                          Number(c.idCliente) ===
                          Number(pago.idCliente)
                      )

                      const vuelo = vuelos.find(
                        (v) =>
                          Number(v.idVuelo) ===
                          Number(pago.idVuelo)
                      )

                      return (
                        <tr
                          key={pago.idPago}
                          className="hover:bg-white/[0.02] transition-colors"
                        >

                          <td className="px-6 py-4 text-sm font-mono text-gray-500">
                            {pago.numeroFactura}
                          </td>

                          <td className="px-6 py-4 text-sm font-medium">
                            {cliente
                              ? formatClienteLabel(cliente)
                              : 'Cliente #' + pago.idCliente}
                          </td>

                          <td className="px-6 py-4 text-sm text-gray-400">
                            {vuelo
                              ? formatVueloLabel(vuelo)
                              : 'Vuelo #' + pago.idVuelo}
                          </td>

                          <td className="px-6 py-4 text-sm text-dorado font-bold">
                            ${Number(pago.monto).toLocaleString()}
                          </td>

                          <td className="px-6 py-4">

                            <span
                              className={
                                'text-xs font-bold uppercase tracking-widest px-3 py-1 rounded-full border ' +
                                getEstadoColor(pago.estado)
                              }
                            >
                              {pago.estado}
                            </span>

                          </td>

                          <td className="px-6 py-4 text-sm text-gray-400">
                            {pago.fechaPago}
                          </td>

                        </tr>
                      )
                    })}

                </tbody>
              </table>

            </div>

          </div>
        </div>
      </main>
    </div>
  )
}
