
package ni.edu.uam.equiposdelaboratorio.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import ni.edu.uam.equiposdelaboratorio.database.JPAUtil;
import ni.edu.uam.equiposdelaboratorio.model.Equipo;
import ni.edu.uam.equiposdelaboratorio.model.Tipo_Equipo;

import java.util.List;

public class EquipoDAO {

    private Tipo_Equipo obtenerTipoEquipo(EntityManager em, Equipo equipo) {
        if (equipo.getTipoEquipo() == null || equipo.getTipoEquipo().getId() == null) {
            throw new RuntimeException("Error al obtener tipo de equipo");
        }
        Long tipoId = equipo.getTipoEquipo().getId();
        Tipo_Equipo tipo = em.find(Tipo_Equipo.class, tipoId);
        if (tipo == null) {
            throw new RuntimeException("El tipo de equipo no existe");
        }
        return tipo;
    }

    public void guardar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Tipo_Equipo tipo = obtenerTipoEquipo(em, equipo);
            equipo.setTipoEquipo(tipo);
            em.persist(equipo);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Equipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Equipo e " + "JOIN FETCH e.tipoEquipo ORDER BY e.id", Equipo.class).getResultList();
        } finally {
            em.close();
        }
    }

    public boolean modificar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipoBD = em.find(Equipo.class, equipo.getId());
            if (equipoBD == null) {
                tx.rollback();
                return false;
            }
            Tipo_Equipo tipo = obtenerTipoEquipo(em, equipo);
            equipoBD.setCodigo(equipo.getCodigo());
            equipoBD.setNombre(equipo.getNombre());
            equipoBD.setMarca(equipo.getMarca());
            equipoBD.setEstado(equipo.getEstado());
            equipoBD.setCantidad(equipo.getCantidad());
            equipoBD.setTipoEquipo(tipo);
            tx.commit();
            return true;

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipoBD = em.find(Equipo.class, id);
            if (equipoBD == null) {
                tx.rollback();
                return false;
            }
            em.remove(equipoBD);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Equipo> buscar(String nombre, Long idTipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Equipo e " + "JOIN FETCH e.tipoEquipo " + "WHERE LOWER(e.nombre) LIKE :nombre";
            if (idTipo != null) {
                jpql += " AND e.tipoEquipo.id = :idTipo";
            }
            jpql += " ORDER BY e.id";
            TypedQuery<Equipo> query = em.createQuery(jpql, Equipo.class);
            String texto = nombre == null ? "" : nombre.trim().toLowerCase();
            query.setParameter("nombre", "%" + texto + "%");
            if (idTipo != null) {
                query.setParameter("idTipo", idTipo);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
