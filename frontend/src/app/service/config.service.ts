import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private _api_url = '/api';
  private _user_url = this._api_url + '/user';

  private _refresh_token_url = this._api_url + '/refresh';

  get refresh_token_url(): string {
    return this._refresh_token_url;
  }

  private _login_url = this._api_url + '/login';

  get login_url(): string {
    return this._login_url;
  }

  private _logout_url = this._api_url + '/logout';

  get logout_url(): string {
    return this._logout_url;
  }

  private _change_password_url = this._api_url + '/changePassword';

  get change_password_url(): string {
    return this._change_password_url;
  }

  private _add_user_url = this._api_url + '/signup';

  get add_user_url(): string {
    return this._add_user_url;
  }

  private _whoami_url = this._api_url + '/whoami';

  get whoami_url(): string {
    return this._whoami_url;
  }

  private _users_url = this._user_url + '/all';

  get users_url(): string {
    return this._users_url;
  }

  private _reset_credentials_url = this._user_url + '/reset-credentials';

  get reset_credentials_url(): string {
    return this._reset_credentials_url;
  }

  private _foo_url = this._api_url + '/foo';

  get foo_url(): string {
    return this._foo_url;
  }

  private _signup_url = this._api_url + '/signup';

  get signup_url(): string {
    return this._signup_url;
  }

  private _temps_url = this._api_url + '/temps';

  get temps_url(): string {
    return this._temps_url;
  }

  private _messages_url = this._api_url + '/messages';

  get messages_url(): string {
    return this._messages_url;
  }

  private _shifts_url = this._api_url + '/shifts';

  get shifts_url(): string {
    return this._shifts_url;
  }

  private _shifts_audit_url = this._api_url + '/shifts/audit';

  get shifts_audit_url(): string {
    return this._shifts_audit_url;
  }

  private _pns_url = this._api_url + '/push';

  get pns_url(): string {
    return this._pns_url;
  }

  private _confirm_url = this._api_url + '/confirmation';

  get confirm_url(): string {
    return this._confirm_url;
  }

  private _remove_url = this._api_url + '/removal';

  get remove_url(): string {
    return this._remove_url;
  }

  private _remove_user_url = this._api_url + '/removeuser';

  get remove_user_url(): string {
    return this._remove_user_url;
  }

  private _clock_url = this._api_url + '/shiftchange';

  get clock_url(): string {
    return this._clock_url;
  }

  private _reset_url = this._api_url + '/reset';

  get reset_url(): string {
    return this._reset_url;
  }

  private _sessions_url = this._api_url + '/sessions';

  get sessions_url(): string {
    return this._sessions_url;
  }


}
