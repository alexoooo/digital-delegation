package ao.dd.shell.impl.transfer.bulk;

import ao.dd.shell.auth.LoginCredential;
import ao.dd.shell.def.TransferAgent;

/**
 * User: aostrovsky
 * Date: 23-Jun-2009
 * Time: 7:36:53 AM
 */
public enum TransferType
{
    //--------------------------------------------------------------------
    SFTP {
        @Override public TransferAgent transferAgent(
                LoginCredential with) {
            return with.newFtp();
        }
    },
    FTP {
        @Override public TransferAgent transferAgent(
                LoginCredential with) {
            return with.newFtp();
        }
    },
    FTPS {
        @Override public TransferAgent transferAgent(
                LoginCredential with) {
            return with.newFtpsApache();
        }
    };


    //--------------------------------------------------------------------
    public abstract TransferAgent transferAgent(LoginCredential with);
}
