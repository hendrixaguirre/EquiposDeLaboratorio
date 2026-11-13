
package ni.edu.uam.equiposdelaboratorio.dao;

import jakarta.persistence.EntityManager;
import ni.edu.uam.equiposdelaboratorio.database.JPAUtil;
import ni.edu.uam.equiposdelaboratorio.model.Tipo_Equipo;

import java.util.List;

public class Tipo_EquipoDAO {

    public List<Tipo_Equipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Tipo_Equipo t", Tipo_Equipo.class).getResultList();
        } finally {
            em.close();
        }
    }
}
