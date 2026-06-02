package devops;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AppPrincipal extends JFrame {

    private JTabbedPane tabbedPane;

    // --- PESTAÑA 1: Registro Pair Programming y Administración ---
    private JComboBox<Usuario> comboProg1, comboProg2;
    private JButton btnAgregarUsuario; 
    private JTextField txtFechaSesion, txtHoraInicio;
    private JComboBox<Tarea> comboTarea;
    private JTextArea txtDescTrabajo;
    private JButton btnGuardarSesion;
    private JTable tablaHistorialSesiones;
    private DefaultTableModel modeloHistorialSesiones;
    private JTextField txtFiltroFechaDesde, txtFiltroFechaHasta;
    private JComboBox<Tarea> comboFiltroTarea;
    private JTextField txtFiltroParticipante;
    private JTable tablaEstadisticasAdmin;
    private DefaultTableModel modeloEstadisticasAdmin;

    // --- PESTAÑA 2: Entorno del Programador ---
    private JTextField txtNuevoIdMerge;
    private JTextField txtRamaOrigen;
    private JTextArea txtCodigoProgramador;
    private JButton btnSoloFinalizarSesion; 
    private JButton btnSoloEnviarMerge;      

    // --- PESTAÑA 3: Operaciones DevOps Pipeline ---
    private JComboBox<String> comboMergeRequests;
    private JTextArea txtAreaConsolaPruebas;
    private JButton btnEjecutarPipeline;
    private JTable tablaHistorialPruebas;
    private DefaultTableModel modeloHistorialPruebas;

    // Bandera de control optimizada para evitar bucles infinitos en listeners
    private boolean ignorarListenersCombo = false;

    public AppPrincipal() {
        setTitle("DevOps Collaboration Suite - SQL Server Integration");
        setSize(1150, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        inicializarTabPairProgramming();
        inicializarTabProgramador();
        inicializarTabPipelineDevOps();

        add(tabbedPane);
        cargarDatosGlobales();
    }

    // =========================================================================
    // PESTAÑA 1: REGISTRO DE PAIR PROGRAMMING
    // =========================================================================
    private void inicializarTabPairProgramming() {
        JPanel panelUS2 = new JPanel(new BorderLayout(10, 10));
        panelUS2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Iniciar Sesión de Trabajo"));
        panelForm.setPreferredSize(new Dimension(380, 600));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboProg1 = new JComboBox<>();
        comboProg2 = new JComboBox<>();
        
        // Listeners corregidos para que no alteren destructivamente la selección del usuario
        comboProg1.addActionListener(e -> ajustarFiltroUsuarios(true));
        comboProg2.addActionListener(e -> ajustarFiltroUsuarios(false));

        btnAgregarUsuario = new JButton("➕ Nuevo Usuario");
        btnAgregarUsuario.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAgregarUsuario.addActionListener(e -> abrirVentanaRegistroUsuario());

        txtFechaSesion = new JTextField(java.time.LocalDate.now().toString());
        txtFechaSesion.setEditable(false); 
        
        txtHoraInicio = new JTextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        txtHoraInicio.setEditable(true); 

        comboTarea = new JComboBox<>();
        txtDescTrabajo = new JTextArea(4, 20);
        txtDescTrabajo.setLineWrap(true);
        txtDescTrabajo.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescTrabajo);
        
        btnGuardarSesion = new JButton("Iniciar Sesión Abierta");
        btnGuardarSesion.setEnabled(false); 

        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel("Conductor (Prog 1) * :"), gbc);
        gbc.gridx = 1; panelForm.add(comboProg1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel("Copiloto (Prog 2) * :"), gbc);
        gbc.gridx = 1; panelForm.add(comboProg2, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; panelForm.add(btnAgregarUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel("Fecha (Automática):"), gbc);
        gbc.gridx = 1; panelForm.add(txtFechaSesion, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panelForm.add(new JLabel("Hora Inicio (Modificable):"), gbc);
        gbc.gridx = 1; panelForm.add(txtHoraInicio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; panelForm.add(new JLabel("Módulo / Tarea * :"), gbc);
        gbc.gridx = 1; panelForm.add(comboTarea, gbc);
        gbc.gridx = 0; gbc.gridy = 6; panelForm.add(new JLabel("Descripción Trabajo:"), gbc);
        gbc.gridx = 1; scrollDesc.setPreferredSize(new Dimension(150, 80)); panelForm.add(scrollDesc, gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; panelForm.add(btnGuardarSesion, gbc);

        panelUS2.add(panelForm, BorderLayout.WEST);

        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel panelHistorialFiltros = new JPanel(new BorderLayout(5, 5));
        panelHistorialFiltros.setBorder(BorderFactory.createTitledBorder("Historial de Colaboración"));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        txtFiltroFechaDesde = new JTextField(7);
        txtFiltroFechaHasta = new JTextField(7);
        comboFiltroTarea = new JComboBox<>();
        txtFiltroParticipante = new JTextField(8);
        
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelFiltros.add(new JLabel("Desde:")); panelFiltros.add(txtFiltroFechaDesde);
        panelFiltros.add(new JLabel("Hasta:")); panelFiltros.add(txtFiltroFechaHasta);
        panelFiltros.add(new JLabel("Tarea:")); panelFiltros.add(comboFiltroTarea);
        panelFiltros.add(new JLabel("Usuario:")); panelFiltros.add(txtFiltroParticipante);
        panelFiltros.add(btnFiltrar); panelFiltros.add(btnLimpiar);
        panelHistorialFiltros.add(panelFiltros, BorderLayout.NORTH);

        String[] columnasSesion = {"ID", "Programador 1", "Programador 2", "Fecha", "Inicio", "Fin", "Módulo Relacionado", "Descripción"};
        modeloHistorialSesiones = new DefaultTableModel(columnasSesion, 0);
        tablaHistorialSesiones = new JTable(modeloHistorialSesiones);
        panelHistorialFiltros.add(new JScrollPane(tablaHistorialSesiones), BorderLayout.CENTER);
        panelDerecho.add(panelHistorialFiltros);

        JPanel panelAdmin = new JPanel(new BorderLayout());
        panelAdmin.setBorder(BorderFactory.createTitledBorder("Métricas del Administrador (KPI de Productividad Colectiva)"));
        
        String[] columnasAdmin = {"Integrante del Equipo", "Sesiones Realizadas", "Tiempo Total Invertido"};
        modeloEstadisticasAdmin = new DefaultTableModel(columnasAdmin, 0);
        tablaEstadisticasAdmin = new JTable(modeloEstadisticasAdmin);
        panelAdmin.add(new JScrollPane(tablaEstadisticasAdmin), BorderLayout.CENTER);
        panelDerecho.add(panelAdmin);

        panelUS2.add(panelDerecho, BorderLayout.CENTER);

        btnGuardarSesion.addActionListener(e -> guardarSesionPairProgramming());
        btnFiltrar.addActionListener(e -> cargarHistorialSesionesFiltrado());
        btnLimpiar.addActionListener(e -> {
            txtFiltroFechaDesde.setText(""); txtFiltroFechaHasta.setText("");
            txtFiltroParticipante.setText(""); comboFiltroTarea.setSelectedIndex(0);
            cargarHistorialSesionesFiltrado();
        });

        txtDescTrabajo.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                btnGuardarSesion.setEnabled(txtDescTrabajo.getText().trim().length() > 0);
            }
        });

        tabbedPane.addTab("1. Registro Pair Programming", panelUS2);
    }

    // CORRECCIÓN: Evita alteración cruzada y limpia los errores de selección cruzada de usuarios
    private void ajustarFiltroUsuarios(boolean cambioConductor) {
        if (ignorarListenersCombo) return;
        
        Usuario seleccionadoProg1 = (Usuario) comboProg1.getSelectedItem();
        Usuario seleccionadoProg2 = (Usuario) comboProg2.getSelectedItem();

        if (seleccionadoProg1 != null && seleccionadoProg2 != null) {
            if (seleccionadoProg1.getId() == seleccionadoProg2.getId()) {
                JOptionPane.showMessageDialog(this, "El Conductor y el Copiloto no pueden ser la misma persona.", "Validación de Integrantes", JOptionPane.WARNING_MESSAGE);
                ignorarListenersCombo = true;
                if (cambioConductor) {
                    comboProg1.setSelectedIndex(-1);
                } else {
                    comboProg2.setSelectedIndex(-1);
                }
                ignorarListenersCombo = false;
            }
        }
    }

    private void abrirVentanaRegistroUsuario() {
        JDialog ventanaPop = new JDialog(this, "Registrar Nuevo Integrante", true);
        ventanaPop.setSize(340, 240);
        ventanaPop.setLocationRelativeTo(this);
        ventanaPop.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        
        // CORRECCIÓN: Se envían estrictamente en MAYÚSCULAS para cumplir la restricción CHECK de tu base de datos
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"DESARROLLADOR", "ADMINISTRADOR"});
        JButton btnGuardar = new JButton("Guardar en SQL Server");

        c.gridx = 0; c.gridy = 0; ventanaPop.add(new JLabel("Nombre:"), c);
        c.gridx = 1; ventanaPop.add(txtNombre, c);
        c.gridx = 0; c.gridy = 1; ventanaPop.add(new JLabel("Email:"), c);
        c.gridx = 1; ventanaPop.add(txtEmail, c);
        c.gridx = 0; c.gridy = 2; ventanaPop.add(new JLabel("Rol:"), c);
        c.gridx = 1; ventanaPop.add(comboRol, c);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; ventanaPop.add(btnGuardar, c);

        btnGuardar.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            String mail = txtEmail.getText().trim();
            String rol = (String) comboRol.getSelectedItem();

            if (nom.isEmpty() || mail.isEmpty()) {
                JOptionPane.showMessageDialog(ventanaPop, "Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String queryInsert = "INSERT INTO usuario (nombre, email, rol) VALUES (?, ?, ?)";
            try (Connection con = ConexionDB.getConexion();
                 PreparedStatement ps = con.prepareStatement(queryInsert)) {
                ps.setString(1, nom);
                ps.setString(2, mail);
                ps.setString(3, rol);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(ventanaPop, "¡Usuario registrado de forma exitosa!");
                ventanaPop.dispose();
                
                // Forzar actualización total inmediata en todas las métricas e historial
                cargarDatosGlobales(); 
                cargarHistorialSesionesFiltrado();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(ventanaPop, "Error al insertar: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        ventanaPop.setVisible(true);
    }

    // =========================================================================
    // PESTAÑA 2: ENTORNO DEL PROGRAMADOR
    // =========================================================================
    private void inicializarTabProgramador() {
        JPanel panelProg = new JPanel(new BorderLayout(10, 10));
        panelProg.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelCampos.setBorder(BorderFactory.createTitledBorder("Datos de la Solicitud"));
        
        txtNuevoIdMerge = new JTextField("MR-" + (int)(Math.random() * 800 + 100), 8);
        txtRamaOrigen = new JTextField("feature-login", 15);
        panelCampos.add(new JLabel("ID Merge Request:")); panelCampos.add(txtNuevoIdMerge);
        panelCampos.add(new JLabel("Rama de Trabajo (Origen):")); panelCampos.add(txtRamaOrigen);

        JPanel panelEditor = new JPanel(new BorderLayout());
        panelEditor.setBorder(BorderFactory.createTitledBorder("Editor de Código Fuente (Simulador de IDE)"));
        
        txtCodigoProgramador = new JTextArea();
        txtCodigoProgramador.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtCodigoProgramador.setText(
            "// Escribe tu funcionalidad Java aquí\n" +
            "public boolean validarAccesoUsuario(String login, String password) {\n" +
            "    if (login.equals(\"root\") && password.equals(\"S3cur3P@ss\")) {\n" +
            "        System.out.println(\"Acceso concedido al servidor de despliegue.\");\n" +
            "        return true;\n" +
            "    }\n" +
            "    return false;\n" +
            "}"
        );
        panelEditor.add(new JScrollPane(txtCodigoProgramador), BorderLayout.CENTER);

        JPanel panelBotonesAccion = new JPanel(new GridLayout(1, 2, 10, 0));
        
        btnSoloFinalizarSesion = new JButton("🛑 Finalizar Sesión Abierta (Código en Pausa)");
        btnSoloFinalizarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        btnSoloEnviarMerge = new JButton("🚀 Enviar Solicitud Merge Request (Código Completado)");
        btnSoloEnviarMerge.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelBotonesAccion.add(btnSoloFinalizarSesion);
        panelBotonesAccion.add(btnSoloEnviarMerge);

        panelProg.add(panelCampos, BorderLayout.NORTH);
        panelProg.add(panelEditor, BorderLayout.CENTER);
        panelProg.add(panelBotonesAccion, BorderLayout.SOUTH);

        btnSoloFinalizarSesion.addActionListener(e -> ejecutarFinalizarSesionPausa());
        btnSoloEnviarMerge.addActionListener(e -> ejecutarEnviarMergeCompleto());

        tabbedPane.addTab("2. Entorno Desarrollador (Enviar Código)", panelProg);
    }

    private void ejecutarFinalizarSesionPausa() {
        String sqlFinalizarSesion = 
            "UPDATE sesion_pair_programming " +
            "SET hora_fin = CONVERT(time, GETDATE()), estado_sesion = 'FINALIZADA' " +
            "WHERE id = (SELECT TOP 1 id FROM sesion_pair_programming WHERE estado_sesion = 'ACTIVA' ORDER BY id DESC)";
        
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement()) {
            
            int afectadas = st.executeUpdate(sqlFinalizarSesion);
            if (afectadas > 0) {
                JOptionPane.showMessageDialog(this, "Sesión de trabajo finalizada y guardada en SQL Server.\nVolviendo al Panel Principal.", "Sesión Cerrada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ninguna sesión activa por cerrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            
            txtCodigoProgramador.setText("// Escribe aquí el siguiente bloque de código...");
            cargarDatosGlobales();
            cargarHistorialSesionesFiltrado();
            
            tabbedPane.setSelectedIndex(0);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cerrar sesión: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarEnviarMergeCompleto() {
        String id = txtNuevoIdMerge.getText().trim();
        String rama = txtRamaOrigen.getText().trim();
        String codigo = txtCodigoProgramador.getText().trim();

        if (id.isEmpty() || rama.isEmpty() || codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar la información del entorno.");
            return;
        }

        // CORRECCIÓN SINTAXIS: Se mapeó rigurosamente a la columna física 'rama_origen' de SQL Server
        String sqlInsert = "INSERT INTO merge_request (id_merge, rama_origen, rama_destino, descripcion, fecha_solicitud, estado) VALUES (?, ?, 'main', ?, GETDATE(), 'PENDIENTE')";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlInsert)) {
            
            ps.setString(1, id);
            ps.setString(2, rama);
            ps.setString(3, codigo);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "¡Merge Request " + id + " enviado con éxito!\nRedirigiendo al Servidor DevOps para pruebas...", "Código Enviado", JOptionPane.INFORMATION_MESSAGE);
            
            txtNuevoIdMerge.setText("MR-" + (int)(Math.random() * 800 + 100));
            txtCodigoProgramador.setText("// Escribe aquí el siguiente bloque de código...");
            
            cargarDatosGlobales(); 

            tabbedPane.setSelectedIndex(2);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fallo al insertar Merge Request: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // PESTAÑA 3: CONTROL DE PIPELINE DEVOPS
    // =========================================================================
    private void inicializarTabPipelineDevOps() {
        JPanel panelDevOps = new JPanel(new BorderLayout(10, 10));
        panelDevOps.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelControlCombo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panelControlCombo.setBorder(BorderFactory.createTitledBorder("Control de Integración Continua"));
        
        comboMergeRequests = new JComboBox<>();
        btnEjecutarPipeline = new JButton("Disparar Pipeline Automático");
        btnEjecutarPipeline.setOpaque(true);
        btnEjecutarPipeline.setContentAreaFilled(true);
        btnEjecutarPipeline.setBorderPainted(true);
        
        panelControlCombo.add(new JLabel("Seleccionar Solicitud para Pruebas:"));
        panelControlCombo.add(comboMergeRequests);
        panelControlCombo.add(btnEjecutarPipeline);

        JPanel panelVisualizadores = new JPanel(new GridLayout(2, 1, 10, 10));

        txtAreaConsolaPruebas = new JTextArea();
        txtAreaConsolaPruebas.setEditable(false);
        txtAreaConsolaPruebas.setBackground(Color.BLACK);
        txtAreaConsolaPruebas.setForeground(new Color(46, 204, 113));
        txtAreaConsolaPruebas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollConsola = new JScrollPane(txtAreaConsolaPruebas);
        scrollConsola.setBorder(BorderFactory.createTitledBorder(null, "Consola de Integración Continua", TitledBorder.LEFT, TitledBorder.TOP, null, Color.DARK_GRAY));

        String[] columnasHistorial = {"ID Historial", "Merge Request", "Fecha Registro", "Estado Validation"};
        modeloHistorialPruebas = new DefaultTableModel(columnasHistorial, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        tablaHistorialPruebas = new JTable(modeloHistorialPruebas);
        JScrollPane scrollHistorialP = new JScrollPane(tablaHistorialPruebas);
        scrollHistorialP.setBorder(BorderFactory.createTitledBorder("Auditoría Histórica de Validaciones en Servidor"));

        panelVisualizadores.add(scrollConsola);
        panelVisualizadores.add(scrollHistorialP);

        panelDevOps.add(panelControlCombo, BorderLayout.NORTH);
        panelDevOps.add(panelVisualizadores, BorderLayout.CENTER);

        btnEjecutarPipeline.addActionListener(e -> ejecutarPipelineTestingSimulado());

        tabbedPane.addTab("3. Servidor DevOps (Pipeline / Testing)", panelDevOps);
    }

    private void guardarSesionPairProgramming() {
        Usuario p1 = (Usuario) comboProg1.getSelectedItem();
        Usuario p2 = (Usuario) comboProg2.getSelectedItem();
        String fecha = txtFechaSesion.getText().trim();
        String hIn = txtHoraInicio.getText().trim();
        Tarea tar = (Tarea) comboTarea.getSelectedItem();
        String desc = txtDescTrabajo.getText().trim();

        if (p1 == null || p2 == null || tar == null) {
            JOptionPane.showMessageDialog(this, "Asegúrese de seleccionar integrantes válidos.", "Faltan Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dml = "INSERT INTO sesion_pair_programming (id_programador1, id_programador2, fecha, hora_inicio, hora_fin, descripcion, id_tarea, estado_sesion) VALUES (?,?,?,?,NULL,?,?,'ACTIVA')";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(dml)) {
            ps.setInt(1, p1.getId());
            ps.setInt(2, p2.getId());
            ps.setString(3, fecha);
            ps.setString(4, hIn);
            ps.setString(5, desc);
            ps.setInt(6, tar.getId());
            ps.executeUpdate();

            txtDescTrabajo.setText("");
            btnGuardarSesion.setEnabled(false);
            txtHoraInicio.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))); 
            
            cargarHistorialSesionesFiltrado();
            actualizarPanelAdministrador(con);
            
            JOptionPane.showMessageDialog(this, "¡Sesión Abierta Iniciada!\nRedirigiendo al Entorno de Desarrollo...");
            
            tabbedPane.setSelectedIndex(1);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fallo al iniciar sesión: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarPipelineTestingSimulado() {
        String targetMerge = (String) comboMergeRequests.getSelectedItem();
        if (targetMerge == null) {
            JOptionPane.showMessageDialog(this, "No hay solicitudes disponibles.");
            return;
        }

        btnEjecutarPipeline.setEnabled(false);
        txtAreaConsolaPruebas.setText("");

        String codigoExtraido = "";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT descripcion FROM merge_request WHERE id_merge = ?")) {
            ps.setString(1, targetMerge);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    codigoExtraido = rs.getString("descripcion").toLowerCase();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        final String codigoParaAnalizar = codigoExtraido;

        Timer timer = new Timer(400, new ActionListener() {
            int tick = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                tick++;
                switch (tick) {
                    case 1:
                        txtAreaConsolaPruebas.append("[PIPELINE] Extrayendo código fuente desde SQL Server: " + targetMerge + "\n");
                        break;
                    case 2:
                        txtAreaConsolaPruebas.append("[ANALIZADOR] Evaluando dependencias...\n");
                        break;
                    case 3:
                        if (!codigoParaAnalizar.contains("{") || !codigoParaAnalizar.contains("}")) {
                            txtAreaConsolaPruebas.append("[ERROR SINTAXIS] Estructura de llaves rota.\n");
                            txtAreaConsolaPruebas.append("[BLOQUEADO] El pipeline detuvo el merge.\n");
                            tick = 99;
                            finalizarPipeline(targetMerge, false, "Fallo de sintaxis.");
                            ((Timer) e.getSource()).stop();
                            btnEjecutarPipeline.setEnabled(true);
                        } 
                        else if (codigoParaAnalizar.contains("login") || codigoParaAnalizar.contains("password")) {
                            txtAreaConsolaPruebas.append("-> testCifradoClaves() => PASSED\n");
                        } else {
                            txtAreaConsolaPruebas.append("-> testCargaEstructuraBasica() => PASSED\n");
                        }
                        break;
                    case 4:
                        txtAreaConsolaPruebas.append("[COMPLETADO] MERGE APROBADO.\n");
                        ((Timer) e.getSource()).stop();
                        finalizarPipeline(targetMerge, true, "Fusión completada con éxito.");
                        btnEjecutarPipeline.setEnabled(true);
                        break;
                }
            }
        });
        timer.start();
    }

    private void finalizarPipeline(String idMerge, boolean esExitoso, String msg) {
        String estadoMR = esExitoso ? "EXITOSO" : "RECHAZADO";
        String estadoVal = esExitoso ? "EXITOSO" : "FALLIDO";

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement("UPDATE merge_request SET estado = ? WHERE id_merge = ?")) {
                ps.setString(1, estadoMR);
                ps.setString(2, idMerge);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO historial_validacion (id_merge, fecha, resultado_pruebas, id_desarrollador, estado_validacion, mensaje) VALUES (?, GETDATE(), ?, 1, ?, ?)")) {
                ps.setString(1, idMerge);
                ps.setString(2, esExitoso ? "PASÓ ESCANEO INTELIGENTE" : "SINTAXIS CORRUPTA");
                ps.setString(3, estadoVal);
                ps.setString(4, msg);
                ps.executeUpdate();
            }

            con.commit();
            cargarDatosGlobales();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarDatosGlobales() {
        try (Connection con = ConexionDB.getConexion()) {
            comboMergeRequests.removeAllItems();
            Statement st = con.createStatement();
            
            ResultSet rs = st.executeQuery("SELECT id_merge FROM merge_request");
            while (rs.next()) {
                comboMergeRequests.addItem(rs.getString("id_merge"));
            }

            // CORRECCIÓN: Activamos la bandera para evitar interferencias de los eventos al poblar datos de SQL Server
            ignorarListenersCombo = true;
            comboProg1.removeAllItems(); comboProg2.removeAllItems();
            rs = st.executeQuery("SELECT id, nombre, email, rol FROM usuario ORDER BY id ASC");
            while (rs.next()) {
                Usuario u = new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"), rs.getString("rol"));
                comboProg1.addItem(u);
                comboProg2.addItem(u);
            }
            if (comboProg2.getItemCount() > 1) comboProg2.setSelectedIndex(1);
            ignorarListenersCombo = false;

            comboTarea.removeAllItems(); comboFiltroTarea.removeAllItems();
            comboFiltroTarea.addItem(new Tarea(0, "TODOS", "", "FILTRO"));
            rs = st.executeQuery("SELECT id, nombre, descripcion, tipo FROM tarea_modulo");
            while (rs.next()) {
                Tarea loopT = new Tarea(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"), rs.getString("tipo"));
                comboTarea.addItem(loopT);
                comboFiltroTarea.addItem(loopT);
            }

            actualizarHistorialPruebas(con);
            cargarHistorialSesionesFiltrado();
            actualizarPanelAdministrador(con);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando componentes: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarHistorialPruebas(Connection con) throws SQLException {
        modeloHistorialPruebas.setRowCount(0);
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_historial, id_merge, fecha, estado_validacion, mensaje FROM historial_validacion ORDER BY id_historial DESC")) {
            while (rs.next()) {
                modeloHistorialPruebas.addRow(new Object[]{
                        rs.getInt("id_historial"), rs.getString("id_merge"),
                        rs.getTimestamp("fecha"), rs.getString("estado_validacion"), rs.getString("mensaje")
                });
            }
        }
    }

    private void cargarHistorialSesionesFiltrado() {
        modeloHistorialSesiones.setRowCount(0);
        String fD = txtFiltroFechaDesde.getText().trim();
        String fH = txtFiltroFechaHasta.getText().trim();
        Tarea tFil = (Tarea) comboFiltroTarea.getSelectedItem();
        String part = txtFiltroParticipante.getText().trim();

        StringBuilder query = new StringBuilder(
            "SELECT s.id, u1.nombre as p1, u2.nombre as p2, s.fecha, s.hora_inicio, s.hora_fin, t.nombre as tarea, s.descripcion " +
            "FROM sesion_pair_programming s " +
            "JOIN usuario u1 ON s.id_programador1 = u1.id " +
            "JOIN usuario u2 ON s.id_programador2 = u2.id " +
            "JOIN tarea_modulo t ON s.id_tarea = t.id WHERE 1=1"
        );

        if (!fD.isEmpty()) query.append(" AND s.fecha >= ?");
        if (!fH.isEmpty()) query.append(" AND s.fecha <= ?");
        if (tFil != null && tFil.getId() != 0) query.append(" AND s.id_tarea = ?");
        if (!part.isEmpty()) query.append(" AND (u1.nombre LIKE ? OR u2.nombre LIKE ?)");
        
        query.append(" ORDER BY s.id DESC");

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (!fD.isEmpty()) ps.setString(paramIndex++, fD);
            if (!fH.isEmpty()) ps.setString(paramIndex++, fH);
            if (tFil != null && tFil.getId() != 0) ps.setInt(paramIndex++, tFil.getId());
            if (!part.isEmpty()) {
                String tk = "%" + part + "%";
                ps.setString(paramIndex++, tk);
                ps.setString(paramIndex++, tk);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Time horaFinDB = rs.getTime("hora_fin");
                    modeloHistorialSesiones.addRow(new Object[]{
                            rs.getInt("id"), rs.getString("p1"), rs.getString("p2"),
                            rs.getDate("fecha"), rs.getTime("hora_inicio"), 
                            (horaFinDB != null) ? horaFinDB : "En curso...", 
                            rs.getString("tarea"), rs.getString("descripcion")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // CORRECCIÓN: Cálculo matemático preciso de tiempos reales de SQL Server acumulados por usuario
    private void actualizarPanelAdministrador(Connection con) throws SQLException {
        modeloEstadisticasAdmin.setRowCount(0);
        String sqlAdmin = 
            "SELECT u.nombre, COUNT(s.id) as total_sesiones, " +
            "SUM(CASE WHEN s.hora_fin IS NOT NULL THEN DATEDIFF(second, s.hora_inicio, s.hora_fin) ELSE 0 END) as total_segundos " +
            "FROM usuario u " +
            "LEFT JOIN sesion_pair_programming s ON (u.id = s.id_programador1 OR u.id = s.id_programador2) " +
            "GROUP BY u.id, u.nombre";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sqlAdmin)) {
            while (rs.next()) {
                int totalSegundos = rs.getInt("total_segundos");
                int totalMinutos = totalSegundos / 60;
                int hrs = totalMinutos / 60;
                int mins = totalMinutos % 60;
                
                modeloEstadisticasAdmin.addRow(new Object[]{
                        rs.getString("nombre"), rs.getInt("total_sesiones"),
                        hrs + " Horas y " + mins + " Minutos"
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new AppPrincipal().setVisible(true);
        });
    }
}