import unittest
from main import calcular_tiempo, generar_estadisticas, PRIORIDAD

class PruebasSoporte(unittest.TestCase):
    def test_tiempo_critica(self):
        self.assertEqual(calcular_tiempo({"prioridad": "critica"}), 4)
    def test_tiempo_baja(self):
        self.assertEqual(calcular_tiempo({"prioridad": "baja"}), 1)
    def test_orden_prioridad(self):
        self.assertLess(PRIORIDAD["critica"], PRIORIDAD["baja"])
    def test_estadisticas_total(self):
        datos = [{"prioridad": "alta", "estado": "asignada", "tecnico": "Ana"},
                 {"prioridad": "baja", "estado": "abierta", "tecnico": "En cola"}]
        self.assertEqual(generar_estadisticas(datos)["total"], 2)
    def test_estadisticas_cola(self):
        datos = [{"prioridad": "baja", "estado": "abierta", "tecnico": "En cola"}]
        self.assertEqual(generar_estadisticas(datos)["en_cola"], 1)

if __name__ == "__main__":
    unittest.main()
