import util.pi_lirc as pi
import unittest


class TestUtilFunctions(unittest.TestCase):

    def test_send_ir_signal(self):
        # Should return true as there exists a remote named Flint with a button title POWER so there should be no error when 
        # sending the code. To further confirm that this works, run it while wathcing the IR blaster to see the led on it 
        # light up.
        self.assertTrue(pi.send_ir_signal('Flint', 'POWER'))

        # Should be false since there does not exist a remote named remote
        self.assertFalse(pi.send_ir_signal('remote', 'POWER'))

        # Should be false since there does not exist a button named button on the remote Flint
        self.assertFalse(pi.send_ir_signal('Flint', 'button'))